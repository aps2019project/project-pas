import java.util.ArrayList;
import java.util.List;

class Deck {
    private List<Card> cards=new ArrayList<>();
    private Unit hero;
    private Item item;

    public List<Card> getCards() {
        return cards;
    }

    public Unit getHero() {
        return hero;
    }

    public Item getItem() {
        return item;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void addToCards(Card newCard){

    }

    public void deleteFromCards(Card card){

    }
}