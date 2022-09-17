package org.trentech.betterbackpacks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Utils {
    public static ItemStack[] deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ItemStack[] contents = null;

        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
        BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);

        contents = (ItemStack[]) objectInputStream.readObject();

        arrayInputStream.close();
        objectInputStream.close();

        return contents;
    }

    public static byte[] serialize(ItemStack[] contents) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

        BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
        objectOutputStream.writeObject(contents);

        arrayOutputStream.close();
        objectOutputStream.close();

        return arrayOutputStream.toByteArray();
    }
}