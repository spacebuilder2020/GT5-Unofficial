package bloodasp.galacticgreg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.ChunkProviderEnd;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderMoon;
import bloodasp.galacticgreg.api.*;
import bloodasp.galacticgreg.api.Enums.AllowedBlockPosition;
import bloodasp.galacticgreg.api.Enums.DimensionType;

/**
 * In this class, you'll find everything you need in order to tell GGreg what to do and where.
 * Everything is done in here. If you're trying to use anything else, you're probably doing something wrong
 * (Or I forgot to add it. In that case, find me on github and create an issue please)
 */
public class ModRegisterer
{
	/**
	 * Just a helper to convert a single element to a list
	 * @param pDef
	 * @return
	 */
	private List<ModDBMDef> singleToList(ModDBMDef pDef)
	{
		List<ModDBMDef> tLst = new ArrayList<ModDBMDef>();
		tLst.add(pDef);
		return tLst;
	}
	
	private static Method registerModContainer;
	/**
	 * Use loose binding of the register-method. Should be enough to 
	 * provide support for GGreg without the requirement to have it in a modpack at all
	 * @param pModContainer
	 */
	public static void registerModContainer(ModContainer pModContainer)
	{
		try {
			registerModContainer.invoke(null, pModContainer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Try to get the instance of GalacticGregs registry in order to register stuff
	 * @return
	 */
	public boolean Init()
	{
		try
		{
			Class gGregRegistry = Class.forName("bloodasp.galacticgreg.registry.GalacticGregRegistry");
			registerModContainer = gGregRegistry.getMethod("registerModContainer", ModContainer.class);
			
			return true;
		}
		catch (Exception e)
		{
			// GalacticGreg is not installed or something is wrong
			return false;
		}
	}
	
	public void Register()
	{
		if (GalacticGreg.GalacticConfig.RegisterVanillaDim)
			registerModContainer(Setup_Vanilla());
		
		if (GalacticGreg.GalacticConfig.RegisterGalacticCraftCore)
			registerModContainer(Setup_GalactiCraftCore());
		
		if (GalacticGreg.GalacticConfig.RegisterGalacticCraftPlanets)
			registerModContainer(Setup_GalactiCraftPlanets());
		
		if (GalacticGreg.GalacticConfig.RegisterGalaxySpace)
			registerModContainer(Setup_GalaxySpace());
	}

	/**
	 * Vanilla MC (End Asteroids)
	 */
	private ModContainer Setup_Vanilla()
	{
		// --- Mod Vanilla (Heh, "mod")
		ModContainer modMCVanilla = new ModContainer("Vanilla");

		// If you happen to have an asteroid dim, just skip the blocklist, and setDimensionType() to DimensionType.Asteroid
		// also don't forget to add at least one asteroid type, or nothing will generate!
		ModDimensionDef dimEndAsteroids = new ModDimensionDef("EndAsteroids", ChunkProviderEnd.class);
		
		dimEndAsteroids.setDimensionType(DimensionType.Asteroid);
		dimEndAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.Netherrack)); 
		dimEndAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.RedGranite)); 
		dimEndAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.BlackGranite));
		dimEndAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.EndStone));
		
		// These Blocks will randomly be generated
		dimEndAsteroids.addSpecialAsteroidBlock(new SpecialBlockComb(Blocks.glowstone));
		dimEndAsteroids.addSpecialAsteroidBlock(new SpecialBlockComb(Blocks.lava, AllowedBlockPosition.AsteroidCore));
		
		modMCVanilla.addDimensionDef(dimEndAsteroids);
		
		return modMCVanilla;
	}
	
	/**
	 * Mod GalactiCraft
	 * Just another setup based on existing classes, due the fact that we're working with GalactiCraft
	 */
	private ModContainer Setup_GalactiCraftCore()
	{
		ModContainer modGCraftCore = new ModContainer("GalacticraftCore");
		ModDBMDef DBMMoon = new ModDBMDef(GCBlocks.blockMoon, 4);

		modGCraftCore.addDimensionDef(new ModDimensionDef("Moon", ChunkProviderMoon.class, singleToList(DBMMoon)));
		
		return modGCraftCore;
	}
	
	
	/**
	 * As GalactiCraftPlanets is an optional mod, don't hardlink it here
	 * @return
	 */
	private ModContainer Setup_GalactiCraftPlanets()
	{
		ModContainer modGCraftPlanets = new ModContainer("GalacticraftMars");
		ModDBMDef DBMMars = new ModDBMDef("tile.mars", 9);
		
		modGCraftPlanets.addDimensionDef(new ModDimensionDef("Mars", "micdoodle8.mods.galacticraft.planets.mars.world.gen.ChunkProviderMars", singleToList(DBMMars)));
		
		ModDimensionDef dimAsteroids = new ModDimensionDef("Asteroids", "micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids");
		dimAsteroids.setDimensionType(DimensionType.Asteroid);
		dimAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.BlackGranite));
		dimAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.RedGranite));
		dimAsteroids.addAsteroidMaterial(new AsteroidBlockComb(GTOreTypes.Netherrack));
		modGCraftPlanets.addDimensionDef(dimAsteroids);
		
		return modGCraftPlanets;		
	}
	
	/**
	 * Mod GalaxySpace by BlesseNtumble
	 */
	private ModContainer Setup_GalaxySpace()
	{
		// First, we create a mod-container that will be populated with dimensions later.
		// The Name must match your modID, as it is checked if this mod is loaded, in order
		// to enable/disable the parsing/registering of dimensions
		ModContainer modCGalaxySpace = new ModContainer("GalaxySpace");

		// Now lets first define a block here for our dimension. You can add the modID, but you don't have to.
		// It will automatically add the mods name that is defined in the modcontainer.
		ModDBMDef DBMPhobos = new ModDBMDef("phobosstone");       
		ModDBMDef DBMDeimos = new ModDBMDef("deimossubgrunt");    
		ModDBMDef DBMCeres = new ModDBMDef("ceressubgrunt");     
		ModDBMDef DBMIO = new ModDBMDef("iorock", 4); // This meta-4 is a copy&paste bug in GSpace and might not work in further versions            
		ModDBMDef DBMEurpoa = new ModDBMDef("europaice");         
		ModDBMDef DBMGanymede = new ModDBMDef("ganymedesubgrunt");  
		ModDBMDef DBMCallisto = new ModDBMDef("callistosubice");    
		ModDBMDef DBMVenus = new ModDBMDef("venussubgrunt");     
		ModDBMDef DBMMercury = new ModDBMDef("mercurycore");       
		ModDBMDef DBMEnceladus = new ModDBMDef("enceladusrock");     
		ModDBMDef DBMTitan = new ModDBMDef("titanstone");        
		ModDBMDef DBMOberon = new ModDBMDef("oberonstone");       
		ModDBMDef DBMProteus = new ModDBMDef("proteusstone");      
		ModDBMDef DBMTriton = new ModDBMDef("tritonstone");       
		ModDBMDef DBMPluto = new ModDBMDef("plutostone");        
		
		// Now define the available dimensions, and their chunkprovider.
		// Same as above, to not have any dependency in your code, you can just give it a string.
		// But it's better to use the actual ChunkProvider class. The Name is used for the GalacticGreg config file.
		// The resulting config setting will be: <ModID>_<Name you give here as arg0>_false = false
		// make sure to never change this name once you've generated your config files, as it will overwrite everything!
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Pluto", "blessentumble.planets.pluto.dimension.ChunkProviderPluto", singleToList(DBMPluto)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Triton", "blessentumble.moons.triton.dimension.ChunkProviderTriton", singleToList(DBMTriton)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Proteus", "blessentumble.moons.proteus.dimension.ChunkProviderProteus", singleToList(DBMProteus)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Oberon", "blessentumble.moons.oberon.dimension.ChunkProviderOberon", singleToList(DBMOberon)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Titan", "blessentumble.moons.titan.dimension.ChunkProviderTitan", singleToList(DBMTitan)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Callisto", "blessentumble.moons.callisto.dimension.ChunkProviderCallisto", singleToList(DBMCallisto)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Ganymede", "blessentumble.moons.ganymede.dimension.ChunkProviderGanymede", singleToList(DBMGanymede)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Ceres", "blessentumble.planets.ceres.dimension.ChunkProviderCeres", singleToList(DBMCeres)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Deimos", "blessentumble.moons.deimos.dimension.ChunkProviderDeimos", singleToList(DBMDeimos)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Enceladus", "blessentumble.moons.enceladus.dimension.ChunkProviderEnceladus", singleToList(DBMEnceladus)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Io", "blessentumble.moons.io.dimension.ChunkProviderIo", singleToList(DBMIO)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Europa", "blessentumble.moons.europa.dimension.ChunkProviderEuropa", singleToList(DBMEurpoa)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Phobos", "blessentumble.moons.phobos.dimension.ChunkProviderPhobos", singleToList(DBMPhobos)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Venus", "blessentumble.planets.venus.dimension.ChunkProviderVenus", singleToList(DBMVenus)));
		modCGalaxySpace.addDimensionDef(new ModDimensionDef("Mercury", "blessentumble.planets.mercury.dimension.ChunkProviderMercury", singleToList(DBMMercury)));
		
		return modCGalaxySpace;
	}
}
