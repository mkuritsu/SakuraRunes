package io.github.itstaylz.sakurarunes.runes;

import io.github.itstaylz.hexlib.utils.StringUtils;

public enum RuneRarity {

    COMMON(StringUtils.fullColorize("#4e4e4e&lC#575757&lO#606060&lM#6a6a6a&lM#737373&lO#7c7c7c&lN")),
    UNCOMMON(StringUtils.fullColorize("#14ff00&lU#11ff15&lN#0efe2a&lC#0bfe3f&lO#09fe54&lM#06fe69&lM#03fd7e&lO#00fd93&lN")),
    RARE(StringUtils.fullColorize("#108cff&lR#0b9dfe&lA#05affe&lR#00c0fd&lE")),
    LEGENDARY(StringUtils.fullColorize("#ffd700&lL#ffc21d&lE#ffad3a&lG#fe9857&lE#fe8374&lN#fe6e91&lD#fe59ae&lA#fd44cb&lR#fd2fe8&lY")),
    MYTHIC(StringUtils.fullColorize("#ce09ff&lM#a52eff&lY#7c52fe&lT#5277fe&lH#299bfd&lI#00c0fd&lC")),
    BOSS(StringUtils.fullColorize("#a20404&lB#a1042e&lO#a10558&lS#a00582&lS"));

    private final String displayName;

    RuneRarity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
