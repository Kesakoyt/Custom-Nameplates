/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api.placeholder.internal;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.api.helper.AdventureHelper;

public class StaticText {

    private final int width;
    private final StaticPosition position;
    private final PreParsedDynamicText text;

    public StaticText(int width, StaticPosition position, String text) {
        this.width = width;
        this.position = position;
        this.text = new PreParsedDynamicText(text);
    }

    public int getWidth() {
        return width;
    }

    public StaticPosition getPosition() {
        return position;
    }

    public PreParsedDynamicText getText() {
        return text;
    }

    public String create() {
        return create(text.fastCreate(null).render(null));
    }

    public String create(CNPlayer player) {
        return create(text.fastCreate(player).render(player));
    }

    public String create(CNPlayer p1, CNPlayer p2) {
        return create(text.fastCreate(p1).render(p2));
    }

    public String create(String text) {
        float parsedWidth = CustomNameplates.getInstance().getAdvanceManager().getLineAdvance(text);
        switch (position) {
            case LEFT -> {
                return text + AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(width - parsedWidth));
            }
            case RIGHT -> {
                return AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(width - parsedWidth)) + text;
            }
            case MIDDLE -> {
                int half = (int) ((width - parsedWidth) / 2);
                String left = AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(half));
                String right = AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(width - parsedWidth - half));
                return left + text + right;
            }
            default -> {
                return "";
            }
        }
    }
}
