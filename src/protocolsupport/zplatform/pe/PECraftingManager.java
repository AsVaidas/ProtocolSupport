package protocolsupport.zplatform.pe;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import protocolsupport.ProtocolSupport;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.serializer.ItemStackSerializer;
import protocolsupport.protocol.serializer.MiscSerializer;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.protocol.typeremapper.itemstack.ItemStackRemapper;
import protocolsupport.utils.IntTuple;
import protocolsupport.zplatform.impl.spigot.itemstack.SpigotNBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.ItemStackWrapper;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PECraftingManager {
    private static PECraftingManager instance = null;

    private static final org.bukkit.inventory.ItemStack AIR = new org.bukkit.inventory.ItemStack(0, 0);

    private ByteBuf byteBuf = Unpooled.buffer();
    private int recipeCount = 0;

    public static PECraftingManager getInstance()    {
        if (instance == null)   {
            instance = new PECraftingManager();
        }
        return instance;
    }

    public PECraftingManager()  {
    }

    public ByteBuf getAllRecipes()   {
        return byteBuf;
    }

    public void registerRecipes()  {
        ProtocolSupport.logInfo("Processing and caching crafting recipes...");
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                Map<Character, org.bukkit.inventory.ItemStack> map = shapedRecipe.getIngredientMap(); //caching for SPEEEEEEED
                String[] pattern = shapedRecipe.getShape(); //caching for SPEEEEEEED
                int width = pattern[0].length(), height = pattern.length;
                ItemStackWrapper[] required = new RecipeItemStackWrapper[width * height];
                for (int z = 0; z < height; ++z) {
                    for (int x = 0; x < width; ++x) {
                        int i = z * x;
                        char key = pattern[z].charAt(x);
                        try {
                            org.bukkit.inventory.ItemStack stack = map.get(key);
                            required[i] = stack == null || stack.getTypeId() < 1 ? RecipeItemStackWrapper.createNull() : fromBukkitStack(stack);
                        } catch (NullPointerException e)    {
                            ProtocolSupport.logInfo("[WARN] Unable to locate key " + key + " for recipe with output " + recipe.getResult().toString());
                            return;
                        }
                    }
                }
                addRecipeShaped(fromBukkitStack(shapedRecipe.getResult()), width, height, required);
            } else if (recipe instanceof ShapelessRecipe)   {
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                ItemStackWrapper[] required = new RecipeItemStackWrapper[shapelessRecipe.getIngredientList().size()];
                for (int i = 0; i < required.length; i++)   {
                    required[i] = fromBukkitStack(shapelessRecipe.getIngredientList().get(i));
                }
                addRecipeShapeless(fromBukkitStack(recipe.getResult()), required);
            } else if (recipe instanceof FurnaceRecipe) {
                FurnaceRecipe shapelessRecipe = (FurnaceRecipe) recipe;
                addRecipeFurnace(fromBukkitStack(shapelessRecipe.getResult()), fromBukkitStack(shapelessRecipe.getInput()));
            } else {
                ProtocolSupport.logInfo("unknown recipe type: " + recipe.getClass().getCanonicalName());
            }
        });

        byte[] cached = byteBuf.array();
        byteBuf.clear();

        VarNumberSerializer.writeVarInt(byteBuf, recipeCount);
        byteBuf.writeBytes(cached);

        ProtocolSupport.logInfo("Done! Processed " + recipeCount + " recipes!");
    }

    public ItemStackWrapper fromBukkitStack(org.bukkit.inventory.ItemStack stack) {
        return new RecipeItemStackWrapper(stack);
    }

    public void addRecipeShaped(ItemStackWrapper output, int width, int height, ItemStackWrapper[] required)    {
        recipeCount++;
        VarNumberSerializer.writeSVarInt(byteBuf, 1); //type
        VarNumberSerializer.writeSVarInt(byteBuf, width);
        VarNumberSerializer.writeSVarInt(byteBuf, height);
        for (ItemStackWrapper stack : required) {
            ItemStackSerializer.writeItemStack(byteBuf, ProtocolVersion.MINECRAFT_PE, "en_US", stack, true);
        }
        VarNumberSerializer.writeVarInt(byteBuf, 1); //not sure but pocketmine has it
        ItemStackSerializer.writeItemStack(byteBuf, ProtocolVersion.MINECRAFT_PE, "en_US", output, true);
        MiscSerializer.writeUUID(byteBuf, UUID.nameUUIDFromBytes(byteBuf.array()));
    }

    public void addRecipeShapeless(ItemStackWrapper output, ItemStackWrapper[] required)    {
        recipeCount++;
        VarNumberSerializer.writeSVarInt(byteBuf, 0);
        VarNumberSerializer.writeVarInt(byteBuf, required.length);
        for (ItemStackWrapper stack : required) {
            ItemStackSerializer.writeItemStack(byteBuf, ProtocolVersion.MINECRAFT_PE, "en_US", stack, true);
        }
        VarNumberSerializer.writeVarInt(byteBuf, 1); //not sure but pocketmine has it
        ItemStackSerializer.writeItemStack(byteBuf, ProtocolVersion.MINECRAFT_PE, "en_US", output, true);
        MiscSerializer.writeUUID(byteBuf, UUID.nameUUIDFromBytes(byteBuf.array()));
    }

    public void addRecipeFurnace(ItemStackWrapper output, ItemStackWrapper input)   {
        IntTuple iddata = ItemStackRemapper.ID_DATA_REMAPPING_REGISTRY.getTable(ProtocolVersion.MINECRAFT_PE).getRemap(input.getTypeId(), input.getData());
        if (iddata != null) {
            input.setTypeId(iddata.getI1());
            if (iddata.getI2() != -1) {
                input.setData(iddata.getI2());
            }
        }

        if (input.getData() == 0) {
            VarNumberSerializer.writeSVarInt(byteBuf, 2); //type
            VarNumberSerializer.writeSVarInt(byteBuf, input.getTypeId());
            ItemStackSerializer.writeItemStack(byteBuf, ProtocolVersion.MINECRAFT_PE, "en_US", output, true);
        } else { //meta recipe
            VarNumberSerializer.writeSVarInt(byteBuf, 3); //type
            VarNumberSerializer.writeSVarInt(byteBuf, input.getTypeId());
            VarNumberSerializer.writeSVarInt(byteBuf, input.getData());
            ItemStackSerializer.writeItemStack(byteBuf, ProtocolVersion.MINECRAFT_PE, "en_US", output, true);
        }
    }

    private static class RecipeItemStackWrapper extends ItemStackWrapper   {
        protected final org.bukkit.inventory.ItemStack itemstack;
        public RecipeItemStackWrapper(org.bukkit.inventory.ItemStack itemstack) {
            this.itemstack = itemstack;
        }

        public static RecipeItemStackWrapper createNull() {
            return create(Material.AIR.getId());
        }

        public static RecipeItemStackWrapper create(int typeId) {
            return new RecipeItemStackWrapper(new org.bukkit.inventory.ItemStack(Material.getMaterial(typeId)));
        }

        @Override
        public org.bukkit.inventory.ItemStack asBukkitMirror() {
            return itemstack;
        }

        @Override
        public boolean isNull() {
            return itemstack == null || itemstack.getType() == Material.AIR;
        }

        @Override
        public int getTypeId() {
            return itemstack.getTypeId();
        }

        @Override
        @SuppressWarnings("deprecation")
        public Material getType() {
            return itemstack.getType();
        }

        @Override
        @SuppressWarnings("deprecation")
        public void setTypeId(int typeId) {
            Material material = Material.getMaterial(typeId);
            itemstack.setType(material == null ? Material.AIR : material);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void setType(Material material) {
            setTypeId(material.getId());
        }

        @Override
        public int getData() {
            return itemstack.getData().getData();
        }

        @Override
        public void setData(int data) {
            itemstack.getData().setData((byte) data);
        }

        @Override
        public int getAmount() {
            return itemstack.getAmount();
        }

        @Override
        public void setAmount(int amount) {
            itemstack.setAmount(amount);
        }

        @Override
        public void setDisplayName(String displayName) {
        }

        @Override
        public SpigotNBTTagCompoundWrapper getTag() {
            return SpigotNBTTagCompoundWrapper.wrap(null);
        }

        @Override
        public void setTag(NBTTagCompoundWrapper tag) {

        }

        @Override
        public ItemStackWrapper cloneItemStack() {
            return new RecipeItemStackWrapper(itemstack.clone());
        }

        @Override
        public int hashCode() {
            return itemstack != null ? itemstack.hashCode() : 0;
        }

        @Override
        public boolean equals(Object otherObj) {
            if (!(otherObj instanceof RecipeItemStackWrapper)) {
                return false;
            }
            RecipeItemStackWrapper other = (RecipeItemStackWrapper) otherObj;
            return Objects.equals(itemstack, other.itemstack);
        }

        @Override
        public String toString() {
            return String.valueOf(itemstack);
        }
    }
}
