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

package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

public interface ActionBarConfig {

    String id();

    Requirement[] requirements();

    CarouselText[] carouselTexts();

    static Builder builder() {
        return new ActionBarConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder requirement(Requirement[] requirements);

        Builder carouselText(CarouselText[] carouselTexts);

        ActionBarConfig build();
    }
}
