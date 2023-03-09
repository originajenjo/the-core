package com.qualityplus.minions.base.minions.minion.layout;

import com.cryptomorin.xseries.XEntity;
import com.cryptomorin.xseries.XMaterial;
import com.qualityplus.minions.base.minions.minion.mob.MinionMob;
import eu.okaeri.configs.OkaeriConfig;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class MinionLayout extends OkaeriConfig {
    private LayoutType type;
    private XMaterial toReplaceBlock;
    private XMaterial toReplaceCrop;
    private List<XMaterial> materialThatCauseException;
    private List<XMaterial> allMaterialsCauseExceptionExcept;


    private MinionMob minionMob;
}
