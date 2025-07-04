package io.sytac.poker.bluffer.model;

public class Card {
    private String rank;
    private String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return this.rank;
    }

    public String getSuit() {
        return this.suit;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Card)) return false;
        final Card other = (Card) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$rank = this.getRank();
        final Object other$rank = other.getRank();
        if (this$rank == null ? other$rank != null : !this$rank.equals(other$rank)) return false;
        final Object this$suit = this.getSuit();
        final Object other$suit = other.getSuit();
        if (this$suit == null ? other$suit != null : !this$suit.equals(other$suit)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Card;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $rank = this.getRank();
        result = result * PRIME + ($rank == null ? 43 : $rank.hashCode());
        final Object $suit = this.getSuit();
        result = result * PRIME + ($suit == null ? 43 : $suit.hashCode());
        return result;
    }

    public String toString() {
        return "Card(rank=" + this.getRank() + ", suit=" + this.getSuit() + ")";
    }
}
