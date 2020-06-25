package xyz.przemyk.gutech.modules.cables;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import xyz.przemyk.gutech.PrzemekTechMod;

import java.util.HashMap;

public class CableNetworksData extends WorldSavedData {
    private static final String DATA_NAME = PrzemekTechMod.MODID + "_CableNetworks";

    public HashMap<Integer, CableNetwork> networks;
    private final World world;

    public CableNetworksData(World world) {
        super(DATA_NAME);
        this.world = world;
        networks = new HashMap<>();
    }

    @Override
    public void read(CompoundNBT nbt) {
        int i = 0;
        CompoundNBT networkNBT = nbt.getCompound(Integer.toString(i));
        while (!networkNBT.isEmpty()) {
            CableNetwork cableNetwork = new CableNetwork(i, world);
            cableNetwork.deserializeNBT(networkNBT);
            networks.put(i, cableNetwork);
            ++i;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        for (int i = 0; i < networks.size(); ++i) {
             compound.put(Integer.toString(i), networks.get(i).serializeNBT());
        }
        return compound;
    }

    public static CableNetworksData get(ServerWorld world) {
        return world.getSavedData().getOrCreate(() -> new CableNetworksData(world), DATA_NAME);
    }

    public CableNetwork createNetwork() {
        CableNetwork cableNetwork = new CableNetwork(networks.size(), world);
        networks.put(networks.size(), cableNetwork);
        return cableNetwork;
    }
}
