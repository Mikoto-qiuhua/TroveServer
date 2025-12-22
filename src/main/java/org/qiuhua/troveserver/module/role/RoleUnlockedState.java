package org.qiuhua.troveserver.module.role;

public enum RoleUnlockedState {

    NotUnlocked("未解锁"),
    Unlocked("已解锁");

    private final String displayName;

    RoleUnlockedState(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
