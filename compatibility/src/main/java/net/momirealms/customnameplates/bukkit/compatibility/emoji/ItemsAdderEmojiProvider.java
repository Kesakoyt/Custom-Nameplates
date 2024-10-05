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

package net.momirealms.customnameplates.bukkit.compatibility.emoji;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.feature.bubble.emoji.EmojiProvider;
import org.bukkit.entity.Player;

public class ItemsAdderEmojiProvider implements EmojiProvider {

    @Override
    public String replace(CNPlayer player, String text) {
        try {
            return FontImageWrapper.replaceFontImages((Player) player.player(), text).replace("§f","<white><font:default>").replace("§r","</font></white>");
        } catch (NoSuchMethodError ignore) {
            return text;
        }
    }
}
