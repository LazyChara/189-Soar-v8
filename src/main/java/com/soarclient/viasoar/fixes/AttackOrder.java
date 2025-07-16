package com.soarclient.viasoar.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class AttackOrder {

	private final static Minecraft mc = Minecraft.getMinecraft();

	public static void sendConditionalSwing(MovingObjectPosition mop) {
		if (ViaHelper.newerThan(ProtocolVersion.v1_8)) {
			if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
				mc.thePlayer.swingItem();
			}
		} else {
			mc.thePlayer.swingItem();
		}
	}

	public static void sendFixedAttack(EntityPlayer entityIn, Entity target) {

		if (ViaHelper.newerThan(ProtocolVersion.v1_8)) {
			mc.playerController.attackEntity(entityIn, target);
			mc.thePlayer.swingItem();
		} else {
			mc.playerController.attackEntity(entityIn, target);
		}
	}
}