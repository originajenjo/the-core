package com.qualityplus.skills.base.stat.stats;

import com.qualityplus.assistant.api.util.IPlaceholder;
import com.qualityplus.assistant.util.StringUtils;
import com.qualityplus.assistant.api.util.MathUtil;
import com.qualityplus.assistant.api.util.NumberUtil;
import com.qualityplus.assistant.util.placeholder.Placeholder;
import com.qualityplus.assistant.util.placeholder.PlaceholderBuilder;
import com.qualityplus.skills.base.skill.gui.GUIOptions;
import com.qualityplus.skills.base.stat.Stat;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public final class StrengthStat extends Stat {
    @Builder
    public StrengthStat(String id, boolean enabled, String displayName, List<String> description, GUIOptions skillGUIOptions, double baseAmount) {
        super(id, enabled, displayName, description, skillGUIOptions, baseAmount);
    }

    @Override
    public List<String> getFormattedDescription(int level) {
        List<IPlaceholder> placeholders = PlaceholderBuilder.create()
                .with(new Placeholder("level_number", level),
                      new Placeholder("level_roman", NumberUtil.toRoman(level)),
                      new Placeholder("chance", 0),
                      new Placeholder("damage_percentage", 0)

                ).get();
        return StringUtils.processMulti(description, placeholders);
    }
}
