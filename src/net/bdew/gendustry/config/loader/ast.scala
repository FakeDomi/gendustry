/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.gencfg.ConfigEntry
import net.bdew.lib.recipes.{CraftingStatement, RecipeStatement, StackBlock, StackRef}

case class FluidSpec(id: String, amount: Int)

// === Modifiers for apiary upgrades ===

abstract class EntryModifier extends ConfigEntry

case class EntryModifierAdd(v: Float) extends EntryModifier

case class EntryModifierSub(v: Float) extends EntryModifier

case class EntryModifierMul(v: Float) extends EntryModifier

case class EntryModifierDiv(v: Float) extends EntryModifier

// === Machine Recipes ===

case class RsMutagen(st: StackRef, mb: Int) extends RecipeStatement

case class RsLiquidDNA(st: StackRef, mb: Int) extends RecipeStatement

case class RsProtein(st: StackRef, mb: Int) extends RecipeStatement

// BC Assembly Table
case class RsAssembly(rec: List[(Char, Int)], power: Int, result: StackRef, cnt: Int) extends CraftingStatement

// Forestry Centrifuge
case class RsCentrifuge(st: StackRef, out: List[(Int, StackRef)], time: Int) extends RecipeStatement

// Forestry Squeezer
case class RsSqueezer(st: StackRef, fluid: FluidSpec, time: Int, remnants: StackRef, chance: Int) extends RecipeStatement

// === Mutations ===

abstract class MutationRequirement

case class RsMutation(parent1: String, parent2: String, result: String, chance: Float, secret: Boolean, requirements: List[MutationRequirement]) extends RecipeStatement

case class MReqTemperature(temperature: String) extends MutationRequirement

case class MReqHumidity(humidity: String) extends MutationRequirement

case class MReqBlock(block: StackBlock) extends MutationRequirement

case class MReqBiome(biome: String) extends MutationRequirement