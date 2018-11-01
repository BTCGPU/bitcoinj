package org.bitcoingoldj.params;

public class EquihashDTO {

    private int _n;
    private int _k;
    private String _person;

    public EquihashDTO(int n, int k, String person) {
        _n = n;
        _k = k;
        _person = person;
    }

    public int getK() {
        return _k;
    }

    public int getN() {
        return _n;
    }

    public String getPerson() {
        return _person;
    }
}
