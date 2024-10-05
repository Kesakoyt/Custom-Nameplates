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

package net.momirealms.customnameplates.api.requirement.builtin;

import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;

public class EndWithRequirement extends PlaceholderRequirement<String> {

    public EndWithRequirement(int refreshInterval, PreParsedDynamicText text, String any) {
        super(refreshInterval, text, any);
    }

    @Override
    protected boolean checkArgument(String a1, String a2) {
        return a1.endsWith(a2);
    }

    @Override
    public String type() {
        return "ends-with";
    }
}
