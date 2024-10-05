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

package net.momirealms.customnameplates.api.storage.data;

import java.util.UUID;

/**
 * The PlayerData class holds data related to a player.
 * It includes the player's name, their nameplate and bubble data.
 */
public class PlayerDataImpl implements PlayerData {

	protected UUID uuid;
	protected String nameplate;
	protected String bubble;

	public PlayerDataImpl(UUID uuid, String nameplate, String bubble) {
		this.uuid = uuid;
		this.nameplate = nameplate;
		this.bubble = bubble;
	}

	@Override
	public String nameplate() {
		return nameplate;
	}

	@Override
	public String bubble() {
		return bubble;
	}

	@Override
	public UUID uuid() {
		return uuid;
	}

	/**
	 * The Builder class provides a fluent API for constructing PlayerData instances.
	 */
	public static class BuilderImpl implements Builder {

		private String nameplate;
		private String bubble;
		private UUID uuid;

		@Override
		public BuilderImpl nameplate(String nameplate) {
			this.nameplate = nameplate;
			return this;
		}

		@Override
		public BuilderImpl bubble(String bubble) {
			this.bubble = bubble;
			return this;
		}

		@Override
		public BuilderImpl uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		@Override
		public PlayerData build() {
			return new PlayerDataImpl(uuid, nameplate, bubble);
		}
	}
}