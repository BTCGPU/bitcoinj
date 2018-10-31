package org.bitcoinj.params;

public class LwmaConfig {
    private int enableHeight;
    private boolean testnet;
    private boolean regtest;
    private int powTargetSpacing;
    private int averagingWindow;
    private int adjustWeight;
    private int minDenominator;
    private boolean solveTimeLimitation;
    private String powLimit;

    public LwmaConfig(int enableHeight, boolean testnet, boolean regtest, int powTargetSpacing, int averagingWindow,
                int adjustWeight, int minDenominator, boolean solveTimeLimitation, String powLimit) {
        this.enableHeight = enableHeight;
        this.testnet = testnet;
        this.regtest = regtest;
        this.powTargetSpacing = powTargetSpacing;
        this.averagingWindow = averagingWindow;
        this.adjustWeight = adjustWeight;
        this.minDenominator = minDenominator;
        this.solveTimeLimitation = solveTimeLimitation;
        this.powLimit = powLimit;
    }

    public int getEnableHeight() {
        return enableHeight;
    }

    public void setEnableHeight(int enableHeight) {
        this.enableHeight = enableHeight;
    }

    public boolean isTestnet() {
        return testnet;
    }

    public boolean isRegtest() {
        return regtest;
    }

    public int getPowTargetSpacing() {
        return powTargetSpacing;
    }

    public int getAveragingWindow() {
        return averagingWindow;
    }

    public int getAdjustWeight() {
        return adjustWeight;
    }

    public int getMinDenominator() {
        return minDenominator;
    }

    public boolean isSolveTimeLimitation() {
        return solveTimeLimitation;
    }

    public String getPowLimit() {
        return powLimit;
    }
}
