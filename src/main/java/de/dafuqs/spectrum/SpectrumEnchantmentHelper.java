package de.dafuqs.spectrum;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectrumEnchantmentHelper {
	
	
	public static ItemStack addOrExchangeEnchantment(ItemStack itemStack, Enchantment enchantment, int level) {
		Identifier enchantmentIdentifier = Registry.ENCHANTMENT.getId(enchantment);
		
		if(itemStack.isOf(Items.BOOK)) {
			ItemStack enchantedBookStack = new ItemStack(Items.ENCHANTED_BOOK, itemStack.getCount());
			enchantedBookStack.setNbt(itemStack.getNbt());
			itemStack = enchantedBookStack;
		}
		
		NbtCompound nbtCompound = itemStack.getOrCreateNbt();
		String nbtString;
		if(itemStack.isOf(Items.ENCHANTED_BOOK)) {
			nbtString = "StoredEnchantments";
		} else {
			nbtString = "Enchantments";
		}
		if (!nbtCompound.contains(nbtString, 9)) {
			nbtCompound.put(nbtString, new NbtList());
		}
		
		NbtList nbtList = nbtCompound.getList(nbtString, 10);
		for(int i = 0; i < nbtList.size(); i++) {
			NbtCompound enchantmentCompound = nbtList.getCompound(i);
			if(enchantmentCompound.contains("id", NbtElement.STRING_TYPE) && Identifier.tryParse(enchantmentCompound.getString("id")).equals(enchantmentIdentifier)) {
				nbtList.remove(i);
				i--;
			}
		}
		
		nbtList.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchantment), (byte)level));
		nbtCompound.put(nbtString, nbtList);
		itemStack.setNbt(nbtCompound);
		
		return itemStack;
	}
	
	public static Map<Enchantment, Integer> collectHighestEnchantments(List<ItemStack> itemStacks) {
		Map<Enchantment, Integer> enchantmentLevelMap = new HashMap();
		
		for(ItemStack itemStack : itemStacks) {
			Map<Enchantment, Integer> itemStackEnchantments = EnchantmentHelper.get(itemStack);
			for(Enchantment enchantment : itemStackEnchantments.keySet()) {
				int level = itemStackEnchantments.get(enchantment);
				if(enchantmentLevelMap.containsKey(enchantment)) {
					int storedLevel = enchantmentLevelMap.get(enchantment);
					if(level > storedLevel) {
						enchantmentLevelMap.put(enchantment, level);
					}
				} else {
					enchantmentLevelMap.put(enchantment, level);
				}
			}
		}
		
		return enchantmentLevelMap;
	}
	
}
