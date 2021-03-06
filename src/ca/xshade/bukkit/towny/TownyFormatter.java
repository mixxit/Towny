package ca.xshade.bukkit.towny;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ca.xshade.bukkit.towny.object.Nation;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.towny.object.TownyIConomyObject;
import ca.xshade.bukkit.towny.object.TownyObject;
import ca.xshade.bukkit.towny.object.TownyWorld;
import ca.xshade.bukkit.util.ChatTools;
import ca.xshade.bukkit.util.Colors;
import ca.xshade.util.StringMgmt;

import com.nijikokun.bukkit.iConomy.iConomy;

//TODO: Make static
//TODO: pull names from the config

public class TownyFormatter {
	public static final SimpleDateFormat lastOnlineFormat = new SimpleDateFormat("MMMMM dd '@' HH:mm");
	public static final SimpleDateFormat registeredFormat = new SimpleDateFormat("MMM d yyyy");
	
	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
		return sdf.format(System.currentTimeMillis());
	}

	public List<String> getStatus(Resident resident) {
		List<String> out = new ArrayList<String>();

		// ___[ King Harlus ]___
		out.add(ChatTools.formatTitle(getFormattedName(resident)));

		// Registered: Sept 3 2009 | Last Online: March 7 @ 14:30
		out.add(Colors.Green + "Registered: " + Colors.LightGreen + registeredFormat.format(resident.getRegistered())
			+ Colors.Gray + " | "
			+ Colors.Green + "Last Online: " + Colors.LightGreen + lastOnlineFormat.format(resident.getLastOnline()));

		// Owner of: 4 Town Blocks | Perm: B=f- D=fa S=f-
		if (resident.getTownBlocks().size() > 0)
			out.add(Colors.Green + "Owner of: " + Colors.LightGreen + resident.getTownBlocks().size() + " plots"
					+ Colors.Gray + " | " + Colors.Green + "Perm: "
					+ Colors.LightGreen + "B=" + Colors.LightGray
					+ (resident.getPermissions().residentBuild ? "f" : "-")
					+ (resident.getPermissions().allyBuild ? "a" : "-")
					+ Colors.LightGreen + " D=" + Colors.LightGray
					+ (resident.getPermissions().residentDestroy ? "f" : "-")
					+ (resident.getPermissions().allyDestroy ? "a" : "-")
					+ Colors.LightGreen + " S=" + Colors.LightGray
					+ (resident.getPermissions().residentDestroy ? "f" : "-")
					+ (resident.getPermissions().allyDestroy ? "a" : "-"));

		// Bank: 534 coins
		if (TownySettings.isUsingIConomy())
			try {
				TownyIConomyObject.checkIConomy();
				out.add(Colors.Green + "Bank: " + Colors.LightGreen + resident.getIConomyBalance() + " " + iConomy.currency);
			} catch (IConomyException e1) {
			}
		
		// Town: Camelot
		String line = Colors.Green + "Town: " + Colors.LightGreen;
		if (!resident.hasTown())
			line += "None";
		else
			try {
				line += getFormattedName(resident.getTown());
			} catch (TownyException e) {
				line += "Error: " + e.getError();
			}
		out.add(line);

		// Friends [12]:
		// James, Carry, Mason
		List<Resident> friends = resident.getFriends();
		out.add(Colors.Green + "Friends " + Colors.LightGreen + "[" + friends.size() + "]" + Colors.Green + ":" + Colors.White + " ");
		out.addAll(ChatTools.listArr(getFormattedNames(friends.toArray(new Resident[0]))));

		return out;
	}

	public List<String> getStatus(Town town) {
		List<String> out = new ArrayList<String>();

		// ___[ Raccoon City (PvP) ]___
		out.add(ChatTools.formatTitle(getFormattedName(town)
				+ (town.isPVP() ? Colors.Red + " (PvP)" : "")));

		// Lord: Mayor Quimby
		// Board: Get your fried chicken
		try {
			out.add(Colors.Green + "Board: " + Colors.LightGreen
					+ town.getTownBoard());
		} catch (NullPointerException e) {
		}

		// Town Size: 0 / 16 [Bonus: 0] [Home: 33,44]
		try {
			out.add(Colors.Green
					+ "Town Size: " + Colors.LightGreen + town.getTownBlocks().size() + " / " + TownySettings.getMaxTownBlocks(town)
					+ Colors.LightBlue + " [Bonus: " + town.getBonusBlocks() + "]"
					+ (town.isPublic() ? Colors.LightGray + " [Home: " + (town.hasHomeBlock() ? town.getHomeBlock().getCoord().toString() : "None") + "]" : ""));
		} catch (TownyException e) {
		}
		
		// Permissions: B=rao D=--- S=ra-
		out.add(Colors.Green + "Permissions: "
				+ Colors.LightGreen + "B=" + Colors.LightGray
				+ (town.getPermissions().residentBuild ? "r" : "-")
				+ (town.getPermissions().allyBuild ? "a" : "-")
				+ (town.getPermissions().outsiderBuild ? "o" : "-")
				+ Colors.LightGreen + " D=" + Colors.LightGray
				+ (town.getPermissions().residentDestroy ? "r" : "-")
				+ (town.getPermissions().allyDestroy ? "a" : "-")
				+ (town.getPermissions().outsiderDestroy ? "o" : "-")
				+ Colors.LightGreen + " S=" + Colors.LightGray
				+ (town.getPermissions().residentDestroy ? "r" : "-")
				+ (town.getPermissions().allyDestroy ? "a" : "-")
				+ (town.getPermissions().outsiderDestroy ? "o" : "-"));
		

		// | Bank: 534 coins
		String bankString = "";
		if (TownySettings.isUsingIConomy())
			try {
				TownyIConomyObject.checkIConomy();
				bankString = Colors.Gray + " | " + Colors.Green + "Bank: " + Colors.LightGreen + town.getIConomyBalance() + " " + iConomy.currency;
			} catch (IConomyException e1) {
			}

		// Mayor: MrSand | Bank: 534 coins
		out.add(Colors.Green + "Mayor: " + Colors.LightGreen + getFormattedName(town.getMayor()) + bankString);
		
		// Assistants: Sammy, Ginger
		if (town.getAssistants().size() > 0)
			out.addAll(ChatTools.listArr(getFormattedNames(town.getAssistants().toArray(new Resident[0])), Colors.Green + "Assistants:" + Colors.White + " "));
		// Nation: Azur Empire
		try {
			out.add(Colors.Green + "Nation: " + Colors.LightGreen
					+ getFormattedName(town.getNation()));
		} catch (TownyException e) {
		}

		// Residents [12]: James, Carry, Mason
		
		out.addAll(ChatTools.listArr(getFormattedNames(town.getResidents().toArray(new Resident[0])),
				Colors.Green + "Residents " + Colors.LightGreen + "[" + town.getNumResidents() + "]" + Colors.Green + ":" + Colors.White + " "));

		return out;
	}

	public List<String> getStatus(Nation nation) {
		List<String> out = new ArrayList<String>();

		// ___[ Azur Empire ]___
		out.add(ChatTools.formatTitle(getFormattedName(nation)));

		// Bank: 534 coins
		if (TownySettings.isUsingIConomy())
			try {
				TownyIConomyObject.checkIConomy();
				out.add(Colors.Green + "Bank: " + Colors.LightGreen + nation.getIConomyBalance() + " " + iConomy.currency);
			} catch (IConomyException e1) {
			}
		
		// King: King Harlus
		if (nation.getNumTowns() > 0 && nation.hasCapital() && nation.getCapital().hasMayor())
			out.add(Colors.Green + "King: " + Colors.LightGreen + getFormattedName(nation.getCapital().getMayor()));
		// Assistants: Mayor Rockefel, Sammy, Ginger
		if (nation.getAssistants().size() > 0)
			out.addAll(ChatTools.listArr(getFormattedNames(nation.getAssistants().toArray(new Resident[0])),
					Colors.Green + "Assistants:" + Colors.White + " "));
		// Towns [44]: James City, Carry Grove, Mason Town
		out.addAll(ChatTools.listArr(getFormattedNames(nation.getTowns().toArray(new Town[0])),
				Colors.Green + "Towns " + Colors.LightGreen + "[" + nation.getNumTowns() + "]" + Colors.Green + ":" + Colors.White + " "));
		// Allies [4]: James Nation, Carry Territory, Mason Country
		out.addAll(ChatTools.listArr(getFormattedNames(nation.getAllies().toArray(new Nation[0])),
				Colors.Green + "Allies " + Colors.LightGreen + "[" + nation.getAllies().size() + "]" + Colors.Green + ":" + Colors.White + " "));
		// Enemies [4]: James Nation, Carry Territory, Mason Country
		out.addAll(ChatTools.listArr(getFormattedNames(nation.getEnemies().toArray(new Nation[0])),
				Colors.Green + "Enemies " + Colors.LightGreen + "[" + nation.getEnemies().size() + "]" + Colors.Green + ":" + Colors.White + " "));

		return out;
	}
	
	public List<String> getStatus(TownyWorld world) {
		List<String> out = new ArrayList<String>();
		
		// ___[ World ]___
		out.add(ChatTools.formatTitle(getFormattedName(world)));
		
		// Claimable: No | PvP: Off
		out.add(Colors.Green + "Claimable: " + (world.isClaimable() ? Colors.LightGreen + "Yes" : Colors.Rose + "No")
				+ Colors.Gray + " | "
				+ Colors.Green + "PvP: " + (world.isPvP() ? Colors.Rose + "On" : Colors.LightGreen + "Off"));
		// Using Default Settings: Yes
		out.add(Colors.Green + "Using Default Settings: " + (world.isUsingDefault() ? Colors.LightGreen + "Yes" : Colors.Rose + "No"));
		// Wilderness:
		//     Build, Destroy, Switch
		//     Ignored Blocks: 34, 45, 64
		out.add(Colors.Green + world.getUnclaimedZoneName() + ":");
		out.add("    " + (world.getUnclaimedZoneBuild() ? Colors.LightGreen : Colors.Rose) + "Build"
				 + Colors.Gray + ", " + (world.getUnclaimedZoneDestroy() ? Colors.LightGreen : Colors.Rose) + "Destroy"
				 + Colors.Gray + ", " + (world.getUnclaimedZoneSwitch() ? Colors.LightGreen : Colors.Rose) + "Switch");
		out.add("    " + Colors.Green + "Ignored Blocks:" + Colors.LightGreen + " " + StringMgmt.join(world.getUnclaimedZoneIgnoreIds(), ", "));
		
		return out;
	}

	public String getNamePrefix(Resident resident) {
		if (resident == null)
			return "";
		if (resident.isKing())
			return TownySettings.getKingPrefix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPrefix(resident);
		return "";
	}
	
	public String getNamePostfix(Resident resident) {
		if (resident == null)
			return "";
		if (resident.isKing())
			return TownySettings.getKingPostfix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPostfix(resident);
		return "";
	}
	
	public String getFormattedName(TownyObject obj) {
		if (obj == null)
			return "Null";
		else if (obj instanceof Resident)
			return getFormattedResidentName((Resident) obj);
		else if (obj instanceof Town)
			return getFormattedTownName((Town) obj);
		else if (obj instanceof Nation)
			return getFormattedNationName((Nation) obj);
		return obj.getName();
	}
	
	public String getFormattedResidentName(Resident resident) {
		if (resident == null)
			return "null";
		if (resident.isKing())
			return TownySettings.getKingPrefix(resident) + resident.getName() + TownySettings.getKingPostfix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPrefix(resident) + resident.getName() + TownySettings.getMayorPostfix(resident);
		return resident.getName();
	}

	public String getFormattedTownName(Town town) {
		if (town.isCapital())
			return TownySettings.getCapitalPrefix(town) + town.getName() + TownySettings.getCapitalPostfix(town);
		return TownySettings.getTownPrefix(town) + town.getName() + TownySettings.getTownPostfix(town);
	}

	public String getFormattedNationName(Nation nation) {
		return TownySettings.getNationPrefix() + nation.getName() + TownySettings.getNationPostfix();
	}

	public String[] getFormattedNames(Resident[] residents) {
		List<String> names = new ArrayList<String>();
		for (Resident resident : residents)
			names.add(getFormattedName(resident));
		return names.toArray(new String[0]);
	}

	public String[] getFormattedNames(Town[] towns) {
		List<String> names = new ArrayList<String>();
		for (Town town : towns)
			names.add(getFormattedName(town));
		return names.toArray(new String[0]);
	}

	public String[] getFormattedNames(Nation[] nations) {
		List<String> names = new ArrayList<String>();
		for (Nation nation : nations)
			names.add(getFormattedName(nation));
		return names.toArray(new String[0]);
	}
}
