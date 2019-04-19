import java.util.ArrayList;
import java.util.List;

class Spell extends Card {
    private Target target;
    private int cooldown;
    private int apChange;
    private int hpChange;
    private List<Buff> addedBuffsToCells = new ArrayList<>();
    private List<Buff> deletedBuffsFromCells = new ArrayList<>();
    private List<Buff> addedBuffsToUnits = new ArrayList<>();
    private List<Buff> deletedBuffsFromUnits = new ArrayList<>();
    private List<Unit> createdUnits = new ArrayList<>();
    private String passiveOrCastable;
    private String description;

    public Target getTarget() {
        return target;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getApChange() {
        return apChange;
    }

    public int getHpChange() {
        return hpChange;
    }

    public String getDescription() {
        return description;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setApChange(int apChange) {
        this.apChange = apChange;
    }

    public void setHpChange(int hpChange) {
        this.hpChange = hpChange;
    }

    public void doSpell(int insertionRow, int insertionColumn) {
        doSpellEffectOnCells(insertionRow, insertionColumn);
        doSpellEffectOnUnits(insertionRow, insertionColumn);
        createUnits(insertionRow, insertionColumn);
    }

    public void doSpellEffectOnCells(int insertionRow, int insertionColumn) {
        List<Cell> targetCells = target.getCells(insertionRow, insertionColumn);
        for (Cell cell : targetCells) {
            for (Buff buff : addedBuffsToCells) {
                cell.getBuffs().add(buff);
            }
            for (Buff buff : deletedBuffsFromCells) {
                cell.getBuffs().remove(buff);
            }
        }
    }

    public void doSpellEffectOnUnits(int insertionRow, int insertionColumn) {
        List<Unit> targetUnits = target.getUnits(insertionRow, insertionColumn);
        for (Unit unit : targetUnits) {
            for (Buff buff : addedBuffsToUnits) {
                unit.getBuffs().add(buff);
            }
            for (Buff buff : deletedBuffsFromUnits) {
                unit.getBuffs().remove(buff);
            }
            unit.changeAp(apChange);
            unit.changeHp(hpChange);
        }
    }

    public void createUnits(int insertionRow, int insertionColumn) {
        List<Cell> targetCells = new ArrayList<>();
        for (Cell cell : targetCells) {
            //todo
        }
    }

    public List<Buff> getAddedBuffsToUnits() {
        return addedBuffsToUnits;
    }

    public void setAddedBuffsToUnits(List<Buff> addedBuffsToUnits) {
        this.addedBuffsToUnits = addedBuffsToUnits;
    }

    public List<Buff> getDeletedBuffsFromUnits() {
        return deletedBuffsFromUnits;
    }

    public void setDeletedBuffsFromUnits(List<Buff> deletedBuffsFromUnits) {
        this.deletedBuffsFromUnits = deletedBuffsFromUnits;
    }
}
