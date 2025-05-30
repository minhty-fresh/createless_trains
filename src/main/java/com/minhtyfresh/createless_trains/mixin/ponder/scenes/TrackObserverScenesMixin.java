package com.minhtyfresh.createless_trains.mixin.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import com.simibubi.create.infrastructure.ponder.scenes.trains.TrackObserverScenes;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrackObserverScenes.class)
public class TrackObserverScenesMixin {

	@Inject(
			method = "observe",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	private static void observe(SceneBuilder scene, SceneBuildingUtil util, CallbackInfo ci) {
		scene.title("track_observer", "Detecting Trains");
		scene.configureBasePlate(1, 1, 9);
		scene.scaleSceneView(0.65F);
		scene.setSceneOffsetY(-1.0F);
		scene.showBasePlate();
		scene.world.toggleControls(util.grid.at(5, 3, 7));
		scene.special.movePointOfInterest(util.grid.at(-10, 2, 7));
		Selection observer = util.select.position(5, 1, 4);
		Selection redstone = util.select.fromTo(5, 1, 3, 5, 1, 2);
		Selection train1 = util.select.fromTo(7, 2, 6, 3, 3, 8);
		Selection train2 = util.select.fromTo(11, 2, 6, 8, 3, 8);
		Selection train2a = util.select.fromTo(11, 2, 3, 8, 3, 5);
		Selection train2b = util.select.fromTo(11, 2, 0, 8, 3, 2);

		for(int i = 10; i >= 0; --i) {
			scene.world.showSection(util.select.position(i, 1, 7), Direction.DOWN);
			scene.idle(1);
		}

		scene.idle(10);
		Vec3 target = util.vector.topOf(5, 0, 7);
		AABB bb = (new AABB(target, target)).move(0.0, 0.125, 0.0);
		scene.overlay.showControls((new InputWindowElement(target, Pointing.DOWN)).rightClick().withItem(AllBlocks.TRACK_OBSERVER.asStack()), 40);
		scene.idle(6);
		scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, bb, 1);
		scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, bb.inflate(0.44999998807907104, 0.0625, 0.44999998807907104), 60);
		scene.idle(10);
		scene.overlay.showText(50).pointAt(target).placeNearTarget().colored(PonderPalette.GREEN).text("Select a Train Track then place the Observer nearby");
		scene.idle(20);
		scene.world.showSection(observer, Direction.DOWN);
		scene.idle(15);
		scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, new AABB(util.grid.at(5, 1, 4)), 20);
		scene.idle(25);
		scene.overlay.showText(70).pointAt(util.vector.blockSurface(util.grid.at(5, 1, 4), Direction.WEST)).attachKeyFrame().placeNearTarget().text("The Observer will detect any Trains passing over the marker");
		scene.idle(20);
		scene.world.showSection(redstone, Direction.SOUTH);
		scene.idle(30);
		ElementLink<WorldSectionElement> trainInstance1 = scene.world.showIndependentSection(train1, Direction.WEST);
		scene.world.moveSection(trainInstance1, util.vector.of(6.0, 0.0, 0.0), 0);
		scene.world.moveSection(trainInstance1, util.vector.of(-16.0, 0.0, 0.0), 80);
		scene.world.animateBogey(util.grid.at(5, 2, 7), 16.0F, 80);
		ElementLink<ParrotElement> birb = scene.special.createBirb(util.vector.centerOf(12, 3, 7), ParrotElement.FacePointOfInterestPose::new);
		scene.special.moveParrot(birb, util.vector.of(-16.0, 0.0, 0.0), 80);
		scene.idle(10);
		ElementLink<WorldSectionElement> trainInstance2 = scene.world.showIndependentSection(train2, Direction.WEST);
		scene.world.moveSection(trainInstance2, util.vector.of(4.0, 0.0, 0.0), 0);
		scene.world.moveSection(trainInstance2, util.vector.of(-14.0, 0.0, 0.0), 70);
		scene.world.animateBogey(util.grid.at(9, 2, 7), 14.0F, 70);
		Selection add = redstone.add(observer);
		scene.idle(13);
		scene.world.toggleRedstonePower(add);
		scene.effects.indicateRedstone(util.grid.at(5, 1, 4));
		scene.idle(20);
		scene.world.hideIndependentSection(trainInstance1, Direction.WEST);
		scene.special.hideElement(birb, Direction.WEST);
		scene.idle(10);
		scene.world.toggleRedstonePower(add);
		scene.idle(5);
		scene.world.hideIndependentSection(trainInstance2, Direction.WEST);
		scene.idle(20);
		target = util.vector.topOf(5, 1, 4);
		new AABB(target, target);
		scene.overlay.showCenteredScrollInput(util.grid.at(5, 1, 4), Direction.UP, 60);
		scene.overlay.showText(80).pointAt(util.vector.topOf(5, 1, 4)).attachKeyFrame().placeNearTarget().text("Observers can be filtered to activate for matching cargo");
		scene.idle(40);

		//EDIT: remove item filtering related to fluid tanks and simplify
		ItemStack coal = new ItemStack(Items.COAL);
		scene.overlay.showControls((new InputWindowElement(target, Pointing.DOWN)).withItem(coal), 30);
		scene.idle(6);
		scene.world.setFilterData(observer, TrackObserverBlockEntity.class, coal);
		scene.idle(50);

		// TODO maybe - add a scene showing an empty train and a train with barrels to illustrate cargo filtering
//		ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
//		scene.overlay.showControls((new InputWindowElement(target, Pointing.DOWN)).withItem(waterBucket), 30);
//		scene.idle(6);
//		scene.world.setFilterData(observer, TrackObserverBlockEntity.class, waterBucket);
//		scene.idle(50);
//		trainInstance1 = scene.world.showIndependentSection(train1, Direction.WEST);
//		scene.world.moveSection(trainInstance1, util.vector.of(6.0, 0.0, 0.0), 0);
//		scene.world.moveSection(trainInstance1, util.vector.of(-16.0, 0.0, 0.0), 80);
//		scene.world.animateBogey(util.grid.at(5, 2, 7), 16.0F, 80);
//		birb = scene.special.createBirb(util.vector.centerOf(12, 3, 7), ParrotElement.FacePointOfInterestPose::new);
//		scene.special.moveParrot(birb, util.vector.of(-16.0, 0.0, 0.0), 80);
//		scene.idle(10);
//		trainInstance2 = scene.world.showIndependentSection(train2b, Direction.WEST);
//		scene.world.moveSection(trainInstance2, util.vector.of(4.0, 0.0, 6.0), 0);
//		scene.world.moveSection(trainInstance2, util.vector.of(-14.0, 0.0, 0.0), 70);
//		scene.world.animateBogey(util.grid.at(9, 2, 1), 14.0F, 80);
//		scene.idle(33);
//		scene.world.hideIndependentSection(trainInstance1, Direction.WEST);
//		scene.special.hideElement(birb, Direction.WEST);
//		scene.idle(10);
//		scene.world.hideIndependentSection(trainInstance2, Direction.WEST);
//		scene.idle(20);
//		trainInstance1 = scene.world.showIndependentSection(train1, Direction.WEST);
//		scene.world.moveSection(trainInstance1, util.vector.of(6.0, 0.0, 0.0), 0);
//		scene.world.moveSection(trainInstance1, util.vector.of(-16.0, 0.0, 0.0), 80);
//		scene.world.animateBogey(util.grid.at(5, 2, 7), 16.0F, 80);
//		birb = scene.special.createBirb(util.vector.centerOf(12, 3, 7), ParrotElement.FacePointOfInterestPose::new);
//		scene.special.moveParrot(birb, util.vector.of(-16.0, 0.0, 0.0), 80);
//		scene.idle(10);
//		trainInstance2 = scene.world.showIndependentSection(train2a, Direction.WEST);
//		scene.world.moveSection(trainInstance2, util.vector.of(4.0, 0.0, 3.0), 0);
//		scene.world.moveSection(trainInstance2, util.vector.of(-14.0, 0.0, 0.0), 70);
//		scene.world.animateBogey(util.grid.at(9, 2, 4), 14.0F, 70);
//		scene.idle(13);
//		scene.world.toggleRedstonePower(add);
//		scene.effects.indicateRedstone(util.grid.at(5, 1, 4));
//		scene.idle(20);
//		scene.world.hideIndependentSection(trainInstance1, Direction.WEST);
//		scene.special.hideElement(birb, Direction.WEST);
//		scene.idle(10);
//		scene.world.toggleRedstonePower(add);
//		scene.idle(5);
//		scene.world.hideIndependentSection(trainInstance2, Direction.WEST);

		ci.cancel();
	}
}
