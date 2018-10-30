package org.bitcoinj.core;

import org.bitcoinj.params.LwmaConfig;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

// https://github.com/BTCGPU/BTCGPU/blob/c919e0774806601f8b192378d078f63f7804b721/src/pow.cpp#L74
public class Lwma {

    public long calcNextBits (Block currentBlock, Block[] previousBlocks, LwmaConfig lwmaConfig) throws Exception {
        if (previousBlocks.length <= lwmaConfig.getAveragingWindow()) {
            throw new IllegalArgumentException("LWMA need the last " + (lwmaConfig.getAveragingWindow() + 1) + " blocks to determine the next target");
        }

        Map<Long, Block> prevBlocks = new HashMap<Long, Block>();
        for (Block b: previousBlocks) {
            prevBlocks.put(b.getHeight(), b);
        }

        for (long i = currentBlock.getHeight() - lwmaConfig.getAveragingWindow() - 1; i < currentBlock.getHeight(); i++) {
            if (!prevBlocks.containsKey(i)) {
                throw new IllegalArgumentException("Block with height " + i + " is missing, cannot calculate next target");
            }
        }

        // loss of precision when converting target to bits, comparing target to target (from bits) will result in different uint256
        BigInteger nextTarget = getLwmaTarget(currentBlock, prevBlocks, lwmaConfig);

        return targetToBits(nextTarget);
    }

    private BigInteger getLwmaTarget (Block cur, Map<Long, Block> prevBlocks, LwmaConfig lwmaConfig) {
        int weight = lwmaConfig.getAdjustWeight();
        long height = cur.getHeight();
        Block prev = prevBlocks.get(height - 1);

        // Special testnet handling
        if (lwmaConfig.isRegtest()) {
            return bitsToTarget(prev.getDifficultyTarget());
        }

        BigInteger limitBig = new BigInteger(lwmaConfig.getPowLimit());
        if (lwmaConfig.isTestnet() && cur.getTimeSeconds() > prev.getTimeSeconds() + lwmaConfig.getPowTargetSpacing() * 2) {
            return limitBig;
        }

        BigInteger totalBig = BigInteger.ZERO;
        long t = 0;
        long j = 0;
        long ts = 6 * lwmaConfig.getPowTargetSpacing();
        BigInteger dividerBig = BigInteger.valueOf(weight * lwmaConfig.getAveragingWindow() * lwmaConfig.getAveragingWindow());

        // Loop through N most recent blocks.  "< height", not "<="
        for (long i = height - lwmaConfig.getAveragingWindow(); i < height; i++) {
            cur = prevBlocks.get(i);
            prev = prevBlocks.get(i - 1);

            long solvetime = cur.getTimeSeconds() - prev.getTimeSeconds();
            if (lwmaConfig.isSolveTimeLimitation() && solvetime > ts) {
                solvetime = ts;
            }

            j += 1;
            t += solvetime * j;
            BigInteger targetBig = bitsToTarget(cur.getDifficultyTarget());
            totalBig = totalBig.add(targetBig.divide(dividerBig));
        }

        // Keep t reasonable in case strange solvetimes occurred.
        if (t < lwmaConfig.getAveragingWindow() * weight / lwmaConfig.getMinDenominator()) {
            t = lwmaConfig.getAveragingWindow() * weight / lwmaConfig.getMinDenominator();
        }

        BigInteger newTargetBig = totalBig.multiply(BigInteger.valueOf(t));
        if (newTargetBig.compareTo(limitBig) >= 0) {
            newTargetBig = limitBig;
        }

        return newTargetBig;
    }

    private BigInteger bitsToTarget(long bits) {
        BigInteger bitsBig = BigInteger.valueOf(bits);
        int size = bitsBig.shiftRight(24).intValue();
        long word = bits & 0x007fffff;

        BigInteger wordBig = BigInteger.valueOf(word);
        if (size <= 3) {
            return wordBig.shiftRight(8 * (3 - size));
        }

        return wordBig.shiftLeft(8 * (size - 3));
    }

    private long targetToBits(BigInteger target) {
        int nsize = ((target.bitLength() + 7) / 8);
        BigInteger cBig = BigInteger.ZERO;

        if (nsize <= 3) {
            cBig = target.shiftLeft(8 * (3 - nsize));
        } else {
            cBig = target.shiftRight(8 * (nsize - 3));
        }

        long c = cBig.longValue();
        if ((c & 0x00800000) != 0) {
            c >>= 8;
            nsize += 1;
        }

        c |= nsize << 24;
        return c;
    }
}
