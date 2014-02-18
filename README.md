ExpoGameEngine
==============

This project is a rework of the game engine for Sugar Tower Defense. The new game is expected to compete in Heartland Gaming Expo 2014.



GAME GUIDE
==============

Basic Premise
=====
This game is a war simulator set in medieval times. The player will begin by selecting a faction and being pitted against an enemy. Each level is a map of a territory with multiple locations that need to be defended or conquered. Owned areas will generate new fighters for the player which can be used for offense or defense. The level is won when the player owns all possible locations in the territory.


Location Types
=====
Basic village: first level of location. Slow to provide new soldiers. Low defensive bonus.
Fortified town: second level location. Faster soldier production and medium defensive bonus.
Keep: highest level. Fastest soldier production and heavy defenses.

-------

Finances
=====
There is no cost to build new soldiers, they are generated automatically based on location levels over time. 
income generated based on owned locations.
Income used to upgrade locations.

-------

Defense
=====
Locations are defended based on the number of soldiers present at the location as well as the location bonus. In order to conquer a location, a superior force must attack and defeat all present soldiers, and have a remaining attack power that is greater than the defensive bonus once all defenders are dead.

Locations may choose to lock doors. This will stop any soldiers from leaving once they have spawned or moved to this location. When attacks are initiated, no soldiers will leave locked locations to assist.

Defending locations will automatically attack any soldiers that pass in their range.

-------

Assault
=====
Assaults are initiated by clicking on a territory that is not currently owned by the player. Soldiers will leave all adjacent territories proportionately to their population until the attacking force size is obtained. They will not wait or group up, they will head straight to the target. It is possible that locations will be abandoned if necessary to fill out the attacking force.

-------

Soldier Movement
=====
Soldiers may be moved to other locations in the same way that an assault force is moved. If an owned location is selected, the movement will be non-hostile. Soldier movement past a hostile location will cause losses as they will be attacked in route.