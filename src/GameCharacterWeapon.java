import processing.data.JSONObject;

/**
 * GameCharacterWeapon Class.
 * Holds information as to a weapon in the game.
 * All of the stats of the weapon are stored here.
 * This does NOT handle the drawing of the weapon.
 * That should be handled in the GameCharacter Class.
 */
class GameCharacterWeapon {

    private int ammoCount;
    private final int ammoCapacity;
    private final int fireRate;
    private final String ammoType;
    private final boolean alternativeFire;

    public GameCharacterWeapon(String name, int ammoCount, Utility util) {

        JSONObject weaponData = util.loadJSONFile("weapons.json").getJSONObject(name);

        this.ammoCapacity = weaponData.getInt("capacity");

        this.ammoCount = Math.min(ammoCount, this.ammoCapacity);

        this.fireRate = weaponData.getInt("fire-rate");

        this.ammoType = weaponData.getString("ammo-type");

        this.alternativeFire = weaponData.getBoolean("alternative-fire");
    }

    public int getAmmoCapacity() {

        return this.ammoCapacity;

    }

    public int getFireRate() {

        return this.fireRate;

    }

    public int getAmmoCount() {

        return this.ammoCount;

    }

    public String getAmmoType() {

        return this.ammoType;

    }

    public void addAmmo(int amountChanged) {
        if (this.ammoCount + amountChanged > this.ammoCapacity)
            this.ammoCount = this.ammoCapacity;
        else
            this.ammoCount += amountChanged;
    }

    public boolean hasAlternativeFire() {

        return this.alternativeFire;

    }

}
