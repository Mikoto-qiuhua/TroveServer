package org.qiuhua.troveserver.arcartx.internal.network.packet.server;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minestom.server.codec.Transcoder;
import net.minestom.server.item.ItemStack;

public class SPackSlotItemStack implements ServerPacketBase{

    @SerializedName(value="id")
    @Getter
    private String id;

    @SerializedName(value="jsonItem")
    @Getter
    private String jsonItem;

    @SerializedName(value="isStartWith")
    @Getter
    private boolean isStartWith;

    @SerializedName(value="isDelete")
    @Getter
    private boolean delete;

    public SPackSlotItemStack(String id, ItemStack itemStack) {
        this.id = id;
        this.jsonItem = ItemStack.CODEC.encode(Transcoder.JSON, itemStack).orElseThrow().toString();
    }

    public SPackSlotItemStack(String id, boolean isStartWith, boolean delete) {
        this.id = id;
        this.isStartWith = isStartWith;
        this.delete = delete;
    }


}
