package net.ludocrypt.limlib.api.world.maze.storage;

import java.util.Map;

import net.ludocrypt.limlib.api.world.maze.MazeGenerator;

public interface MazeStorageProvider {

	public Map<String, MazeGenerator> generators();

}
