package com.soarclient.libraries.soarium.culling;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

import com.soarclient.libraries.occlusionculling.OcclusionCullingInstance;
import com.soarclient.libraries.occlusionculling.util.Vec3d;
import com.soarclient.libraries.soarium.Soarium;
import com.soarclient.libraries.soarium.culling.access.Cullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

public class CullTask implements Runnable {

	public boolean requestCull = false;

	private final OcclusionCullingInstance culling;
	private final Minecraft client = Minecraft.getMinecraft();
	private final int sleepDelay = Soarium.getConfig().culling.sleepDelay;
	private final int hitboxLimit = Soarium.getConfig().culling.hitboxLimit;
	private final Set<String> unCullable;
	public long lastTime = 0;

	// reused preallocated vars
	private Vec3d lastPos = new Vec3d(0, 0, 0);
	private Vec3d aabbMin = new Vec3d(0, 0, 0);
	private Vec3d aabbMax = new Vec3d(0, 0, 0);

	public CullTask(OcclusionCullingInstance culling, Set<String> unCullable) {
		this.culling = culling;
		this.unCullable = unCullable;
	}

	@Override
	public void run() {
		while (client.isRunning()) {
			try {
				Thread.sleep(sleepDelay);

				if (SoariumEntityCulling.enabled && client.theWorld != null && client.thePlayer != null
						&& client.thePlayer.ticksExisted > 10 && client.getRenderViewEntity() != null) {
					net.minecraft.util.Vec3 cameraMC = client.thePlayer.getPositionEyes(0);
					if (requestCull || !(cameraMC.xCoord == lastPos.x && cameraMC.yCoord == lastPos.y
							&& cameraMC.zCoord == lastPos.z)) {
						long start = System.currentTimeMillis();
						requestCull = false;
						lastPos.set(cameraMC.xCoord, cameraMC.yCoord, cameraMC.zCoord);
						Vec3d camera = lastPos;
						culling.resetCache();
						boolean noCulling = client.thePlayer.isSpectator() || client.gameSettings.thirdPersonView != 0;
						Iterator<TileEntity> iterator = client.theWorld.loadedTileEntityList.iterator();
						TileEntity entry;
						while (iterator.hasNext()) {
							try {
								entry = iterator.next();
							} catch (NullPointerException | ConcurrentModificationException ex) {
								break; // We are not synced to the main thread, so NPE's/CME are allowed here and way
										// less
								// overhead probably than trying to sync stuff up for no really good reason
							}
							if (unCullable.contains(entry.getBlockType().getUnlocalizedName())) {
								continue;
							}
							Cullable cullable = (Cullable) entry;
							if (!cullable.isForcedVisible()) {
								if (noCulling) {
									cullable.setCulled(false);
									continue;
								}
								BlockPos pos = entry.getPos();
								if (pos.distanceSq(new Vec3i(cameraMC.xCoord, cameraMC.yCoord, cameraMC.zCoord)) < 64
										* 64) { // 64 is the fixed max tile view distance
									aabbMin.set(pos.getX(), pos.getY(), pos.getZ());
									aabbMax.set(pos.getX() + 1d, pos.getY() + 1d, pos.getZ() + 1d);
									boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
									cullable.setCulled(!visible);
								}

							}
						}
						Entity entity = null;
						Iterator<Entity> iterable = client.theWorld.weatherEffects.iterator();
						while (iterable.hasNext()) {
							try {
								entity = iterable.next();
							} catch (NullPointerException | ConcurrentModificationException ex) {
								break; // We are not synced to the main thread, so NPE's/CME are allowed here and way
										// less
										// overhead probably than trying to sync stuff up for no really good reason
							}
							if (entity == null || !(entity instanceof Cullable)) {
								continue; // Not sure how this could happen outside from mixin screwing up the inject
											// into Entity
							}
							Cullable cullable = (Cullable) entity;
							if (!cullable.isForcedVisible()) {
								if (noCulling || isSkippableArmorstand(entity)) {
									cullable.setCulled(false);
									continue;
								}
								if (entity.getPositionVector()
										.squareDistanceTo(cameraMC) > Soarium.getConfig().culling.tracingDistance
												* Soarium.getConfig().culling.tracingDistance) {
									cullable.setCulled(false); // If your entity view distance is larger than
																// tracingDistance just render it
									continue;
								}
								AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
								if (boundingBox.maxX - boundingBox.minX > hitboxLimit
										|| boundingBox.maxY - boundingBox.minY > hitboxLimit
										|| boundingBox.maxZ - boundingBox.minZ > hitboxLimit) {
									cullable.setCulled(false); // To big to bother to cull
									continue;
								}
								aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
								aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
								boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
								cullable.setCulled(!visible);
							}
						}
						lastTime = (System.currentTimeMillis() - start);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isSkippableArmorstand(Entity entity) {
		if (!Soarium.getConfig().culling.skipMarkerArmorStands)
			return false;
		return entity instanceof EntityArmorStand && ((EntityArmorStand) entity).hasMarker();
	}
}
