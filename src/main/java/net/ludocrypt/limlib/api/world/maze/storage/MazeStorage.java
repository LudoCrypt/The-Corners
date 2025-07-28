package net.ludocrypt.limlib.api.world.maze.storage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.MathHelper;

public class MazeStorage {

	private static final Logger LOGGER = LogUtils.getLogger();

	private final Map<String, MazeGenerator> generators;

	private Queue<Runnable> dirt = new LinkedList<Runnable>();

	private File dir;

	public MazeStorage(Map<String, MazeGenerator> generators, File dir) {
		this.generators = generators;
		this.dir = dir;
		dir.mkdirs();

		for (Entry<String, MazeGenerator> entry : generators.entrySet()) {
			new File(dir, entry.getKey()).mkdirs();
			entry.getValue().connect((pos, maze) -> dirt.add(() -> serialize(entry.getKey(), pos, maze)));
		}

	}

	public void read() {

		for (String id : generators.keySet()) {
			File mazeDir = new File(dir, id);

			for (File data : mazeDir.listFiles((file) -> FilenameUtils.getExtension(file.getAbsolutePath()).equals("nbt"))) {

				try {
					NbtCompound region = NbtIo.readCompressed(data);

					String[] regionPosRaw = FilenameUtils.getBaseName(data.getAbsolutePath()).substring(2).split("\\.");
					Vec2i regionPos = new Vec2i(Integer.valueOf(regionPosRaw[0]), Integer.valueOf(regionPosRaw[1])).mul(16);

					for (String mid : region.getKeys()) {
						String[] posRaw = mid.split("\\.");
						Vec2i pos = new Vec2i(Integer.valueOf(posRaw[0]), Integer.valueOf(posRaw[1]));

						NbtCompound mazeRaw = region.getCompound(mid);

						this.generators
							.get(id)
							.getMazes()
							.put(regionPos.add(pos), new MazeComponent(mazeRaw.getInt("width"), mazeRaw.getInt("height"))
								.read(mazeRaw.getCompound("maze")));
					}

				} catch (IOException | NullPointerException e) {
					LOGGER.error("Could not read data {}", this, e);
				}

			}

		}

	}

	public void save() {
		Runnable serializer;

		while ((serializer = dirt.poll()) != null) {
			serializer.run();
		}

	}

	public void serialize(String mazeId, Vec2i pos, MazeComponent maze) {

		try {
			File regionFile = new File(new File(dir, mazeId), "m." + ((pos.getX() - MathHelper
				.floorMod(pos.getX(), 16)) / 16) + "." + ((pos.getY() - MathHelper.floorMod(pos.getY(), 16)) / 16) + ".nbt");

			NbtCompound region = new NbtCompound();

			if (regionFile.exists()) {
				region = NbtIo.readCompressed(regionFile);
			}

			NbtCompound compound = new NbtCompound();
			NbtCompound mazeCompound = maze.write();

			compound.put("maze", mazeCompound);
			compound.putInt("width", maze.width);
			compound.putInt("height", maze.height);

			region.put(MathHelper.floorMod(pos.getX(), 16) + "." + MathHelper.floorMod(pos.getY(), 16), compound);

			NbtIo.writeCompressed(region, regionFile);

		} catch (IOException e) {
			LOGGER.error("Could not save data {}", this, e);
		}

	}

}
