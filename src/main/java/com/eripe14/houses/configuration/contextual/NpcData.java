package com.eripe14.houses.configuration.contextual;

import net.dzikoysk.cdn.entity.Contextual;

@Contextual
public class NpcData {

    private String name;
    private String skinTextureValue;
    private String skinSignatureValue;

    private NpcData() { }

    public NpcData(String name, String skinTextureValue, String skinSignatureValue) {
        this.name = name;
        this.skinTextureValue = skinTextureValue;
        this.skinSignatureValue = skinSignatureValue;
    }

    public String getName() {
        return this.name;
    }

    public String getSkinTextureValue() {
        return this.skinTextureValue;
    }

    public String getSkinSignatureValue() {
        return this.skinSignatureValue;
    }

}