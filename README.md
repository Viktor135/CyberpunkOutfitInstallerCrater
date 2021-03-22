```
      ::::::::       :::::::::       ::::::::       :::::::::::       :::::::: 
    :+:    :+:      :+:    :+:     :+:    :+:          :+:          :+:    :+: 
   +:+             +:+    +:+     +:+    +:+          +:+          +:+         
  +#+             +#++:++#+      +#+    +:+          +#+          +#+          
 +#+             +#+            +#+    +#+          +#+          +#+           
#+#    #+#      #+#            #+#    #+#          #+#          #+#    #+#     
########       ###             ########       ###########       ########       
```


# CyberpunkOutfitInstallerCrater (CpOIC)

CpOIC is an easy-to-use tool to generate mod installers for (mainly) clothing mods for cyberpunk.

Features:
* Easily creates installers for clothing mods
* Therefore, easy installation and uninstallation of clothing mods
* An Easy way to change the installed clothing variant of a mod. 
* Handling of clothing mod conflicts (only if both conflicting mods are using CpOIC)

Isn't it a hassle...

You see a nice clothing mod for Cyberpunk at [nexusmods.com](https://www.nexusmods.com), it offers replacements for
Johnnies pants and shoes in different Variants and colors. But which one you should take? The Red short skirt?
The blue pants? Which shoe model and color? Maybe you chose one, copied the .archive files to the Cyberpunk directory
but then you think another one would be better. And again, manual copy and paste...

This tool allows the moder to easily create a mod installer. Then the user can download the mod and Vortex will open the
installer at mod installation. There the user can choose which version he fancy installing.
Also, the CpOIC takes care about the overwritten game item, so the user is warned, when he installs a mod which will 
replace the same game item.

# About mod installer
The [Vortex](https://www.nexusmods.com/site/mods/1) mod manager allows the moder to create mod installers
([also refered to as FOMOD installer](https://wiki.nexusmods.com/index.php/How_to_create_mod_installers)) 
which made the mod users live much easier. It essentially adds a menu to the mod installation where one can choose 
which variant of a mod should be installed.

At this moment in time the Cyberpunk mod community has no way to simply add a new item to the game. Therefore, they have
to replace an existing item and this introduces a lot of problems.
* Firstly one should not install multiple mods which overwrite the same item at one time.
* Without a mod installer you have to manually copy the mods into your game directory.
  This is very often the case in Cyberpunk and at some point in time you will lose track about which mod is installed at 
  the moment.
* Normally it is tedious to create such an installer, because it is very repetitive.
But the CpOIC allows to generate such an installer with in only a few minutes
  
# How to use?
``` java
My fancy mod/                                   // <1> Should have the name of your mod
├── Replace Johnnies pants/                     // <2> One directory per replaced game item 
│   ├── Short skirt/                            // <3> One directory per variant
│   │   ├── basegame_Short_Skirt_Red.archive    // <4> The archive containing the mod files 
│   │   ├── basegame_Short_Skirt_Red.png        // <5> One image per .archive
│   │   ├── basegame_Short_Skirt_Blue.archive
│   │   ├── basegame_Short_Skirt_Blue.png
│   │   ├── description.txt                     // <6> Maybe an description. E.g.: "please select one color for the short skirt"
│   │   └── Items.Q005_Johnny_Pants             // <7> A file with the code of the item a archive will replace.
│   │
│   ├── Long skirt/
│   │   ├── basegame_Long_Skirt_Red.archive
│   │   ├── basegame_Long_Skirt_Blue.archive
│   │   ├── common.png                          // <5> An common image for all archives in one folder
│   │   ├── description.txt                     
│   │   └── Items.Q005_Johnny_Pants
│   │
│   └── description.txt                         // <6> E.g.: "Would you reather like to replace Johnnies pants by a short or long skirt?"
│
├── Replace Johnnies boots/
│   ├── basegame_Sneaker_Green.archive     
│   ├── basegame_Sneaker_Blue.archive        
│   ├── common.png   
│   ├── Items.Q005_Johnny_Shoes
│   └── description.txt  
│
├── common.png                                  // <5> The main image of the mod
├── SelectAny                                   // <8> Enables the user to replace multiple items
└── description.txt                             // <6> E.g. "This is my fancy mod! Pleas select whether you like to replace Johnnies pants and / or boots"
```

* __<1>__  The so called *workspace* which should have the name of the mod.
* __<2>__  For every game item the mod can replace one should create one Folder in the workspace.
  If only one item can be replaced (e.g. the mod only offers certain variants for Johnnies Pants) this layer can be left out.
* __<3>__  Directories for certain variants. This layer can be skipped if only one variant is offered (see: `Replace Johnnies boots/`).
* __<4>__  The archive which contains the modification. They will be copied in to the game folder (under `/archive/pc/patch/`).
* __<5>__  For the images there are two possibilities:
  * One can define one image per *.archive*. Then the image should have the same name as the archive, except the ending.
  * One can define one common image for all *.archive* files. This should be named `common.<Image ending>`
* __<6>__  In every folder a description can be added.  This should be named `description.txt`
* __<7>__  Every directory containing archives should contain a file named exactly the same as the game item which is
  replaced by an archive. The content of the file does not mather.
  __Attention:__ The file should not have an ending like *txt* ore something else. It should have __exactly__
  the same name as the game item.
* __<8>__ By default, the user can only choose one or none option, if the behaviour should be other than that,
create a file __without__ file ending and name it
  *SelectAtLeastOne*,   *SelectAtMostOne*,   *SelectExactlyOne*,   *SelectAll* or  *SelectAny*.
  This is useful to allow the user, like in the example, to replace Johnnies pants *and*, *or* boots.
  