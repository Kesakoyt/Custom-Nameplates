package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.scheduler.CancellableTask;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.util.FakeEntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeamPreviewSimpleEntity {

    private final TeamPlayer teamPlayer;
    private final UUID uuid = UUID.randomUUID();
    private final int entityId;
    private CancellableTask tpTask;

    public TeamPreviewSimpleEntity(TeamPlayer teamPlayer) {
        this.teamPlayer = teamPlayer;
        this.entityId = FakeEntityUtils.getAndIncrease();
    }

    private String getTagString() {
        TeamColor teamColor = CustomNameplatesPlugin.get().getNameplateManager().getTeamColor(teamPlayer.getPlayer());
        if (teamColor == TeamColor.NONE || teamColor == TeamColor.CUSTOM)
            teamColor = TeamColor.WHITE;
        return teamPlayer.getPrefix().getLatestValue(teamPlayer.getPlayer()) +
                "<" + teamColor.name() + ">" +
                teamPlayer.getPlayer().getName() +
                "</" + teamColor.name() + ">" +
                teamPlayer.getSuffix().getLatestValue(teamPlayer.getPlayer());
    }

    public void spawn() {
        teamPlayer.getSuffix().updateForViewer(teamPlayer.getPlayer());
        teamPlayer.getPrefix().updateForViewer(teamPlayer.getPlayer());
        PacketManager.getInstance().send(teamPlayer.getPlayer(), getSpawnPackets(getTagString()));
        this.tpTask = CustomNameplatesPlugin.get().getScheduler().runTaskAsyncTimer(
                () -> PacketManager.getInstance().send(teamPlayer.getPlayer(), getTeleportPacket()),
                50,
                50,
                TimeUnit.MILLISECONDS
        );
    }

    public void destroy() {
        if (this.tpTask != null && !this.tpTask.isCancelled()) {
            this.tpTask.cancel();
        }
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        PacketManager.getInstance().send(teamPlayer.getPlayer(), destroyPacket);
        teamPlayer.getSuffix().removeViewer(teamPlayer.getPlayer());
        teamPlayer.getPrefix().removeViewer(teamPlayer.getPlayer());
    }

    public void update() {
        if (teamPlayer.getPrefix().updateForViewer(teamPlayer.getPlayer()) | teamPlayer.getSuffix().updateForViewer(teamPlayer.getPlayer()))
            PacketManager.getInstance().send(teamPlayer.getPlayer(), getMetaPacket(getTagString()));
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUUID() {
        return uuid;
    }

    public PacketContainer getTeleportPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = teamPlayer.getPlayer().getLocation();
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY() + 0.8);
        packet.getDoubles().write(2, location.getZ());
        return packet;
    }

    protected PacketContainer getMetaPacket(String text) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, entityId);
        String json = AdventureManagerImpl.getInstance().componentToJson(AdventureManagerImpl.getInstance().getComponentFromMiniMessage(text));
        if (CustomNameplatesPlugin.getInstance().getVersionManager().isVersionNewerThan1_19_R2()) {
            WrappedDataWatcher wrappedDataWatcher = createArmorStandDataWatcher(json);
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> wrappedDataValueList.add(new WrappedDataValue(entry.getWatcherObject().getIndex(), entry.getWatcherObject().getSerializer(), entry.getRawValue())));
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, createArmorStandDataWatcher(json).getWatchableObjects());
        }
        return metaPacket;
    }

    private WrappedDataWatcher createArmorStandDataWatcher(String json) {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(json).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        byte flag = 0x20;
        if (teamPlayer.getPlayer().isSneaking()) flag += (byte) 0x02;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        return wrappedDataWatcher;
    }

    private PacketContainer[] getSpawnPackets(String text) {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, entityId);
        entityPacket.getModifier().write(1, uuid);
        entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = teamPlayer.getPlayer().getLocation();
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY() + 0.8);
        entityPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = getMetaPacket(text);
        return new PacketContainer[] {entityPacket, metaPacket};
    }
}