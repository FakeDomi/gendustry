/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.extractor

import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.gendustry.config.{Fluids, Items}
import net.bdew.gendustry.fluids.LiquidDNASources
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.data._
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.power.TileBaseProcessor
import net.bdew.lib.tile.ExposeTank
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids._

class TileExtractor
    extends TileBaseProcessor
    with TileWorker
    with TilePowered
    with ExposeTank
    with TileCoverable
    with TileKeepData {
  lazy val cfg = MachineExtractor

  object slots {
    val inIndividual = 0
    val inLabware = 1
  }

  val tank = DataSlotTankRestricted("tank", this, cfg.tankSize, Fluids.dna)
    .setUpdate(UpdateKind.GUI, UpdateKind.SAVE)
  val output = DataSlotInt("output", this).setUpdate(UpdateKind.SAVE)

  def getSizeInventory = 2

  def getTankFromDirection(dir: ForgeDirection): IFluidTank = tank

  def isWorking = output > 0

  def tryStart(): Boolean = {
    if (
      getStackInSlot(slots.inIndividual) != null && getStackInSlot(
        slots.inLabware
      ) != null
    ) {
      output := LiquidDNASources.getValue(getStackInSlot(0))
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(slots.inLabware, 1)
      decrStackSize(0, 1)
      return true
    } else return false
  }

  def tryFinish(): Boolean = {
    if (tank.fill(output, false) == output.value) {
      tank.fill(output, true)
      output := -1
      return true
    } else return false
  }

  def sendFluid() {
    for (dir <- ForgeDirection.VALID_DIRECTIONS) {
      val te: TileEntity = worldObj.getTileEntity(
        xCoord + dir.offsetX,
        yCoord + dir.offsetY,
        zCoord + dir.offsetZ
      )
      if (te != null && te.isInstanceOf[IFluidHandler]) {
        val pumped = te
          .asInstanceOf[IFluidHandler]
          .fill(dir.getOpposite, tank.getFluid.copy(), true)
        if (pumped > 0) {
          tank.drain(pumped, true)
          if (tank.getFluidAmount <= 0) return
        }
      }
    }
  }

  override def tickServer() {
    super.tickServer()
    if (tank.getFluidAmount > 0) sendFluid()
  }

  allowSided = true
  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean =
    slot match {
      case slots.inIndividual => LiquidDNASources.getValue(stack) > 0
      case slots.inLabware    => stack.getItem == Items.labware
      case _                  => false
    }
  override def canExtractItem(slot: Int, item: ItemStack, side: Int): Boolean =
    false

  override def fill(
      from: ForgeDirection,
      resource: FluidStack,
      doFill: Boolean
  ) = 0
  override def canFill(from: ForgeDirection, fluid: Fluid) = false

  override def isValidCover(side: ForgeDirection, cover: ItemStack) = true
}
