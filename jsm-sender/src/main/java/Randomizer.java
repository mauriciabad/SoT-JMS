import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Randomizer {

  public static String getName() {
    return names.get((int) (Math.random() * names.size()));
  }

  public static Color getColor() {
    int r = (int) (Math.random()*256);
    int g = (int) (Math.random()*256);
    int b = (int) (Math.random()*256);

    if((299*r + 587*g + 114*b) / 1000 >= 4) return new Color(r, g, b);
    else return getColor();
  }

  private static Random random = new Random();
  private static ArrayList<String> names = new ArrayList<String>(
    Arrays.asList(
    "Hollandgenix",
    "Goava",
    "Movegenix",
    "Hollandsio",
    "Goprism",
    "Hollandaholic",
    "Moveshack",
    "Flyry",
    "Hollandjet",
    "Touristdeck",
    "Movenest",
    "Moveegy",
    "Flyporium",
    "Gosquad",
    "Hollandify",
    "Hollandzoid",
    "Flyava",
    "Touristtap",
    "Flyio",
    "Touriststation",
    "Flyworks",
    "Goegy",
    "Touristegy",
    "Touristfluent",
    "Touriststop",
    "Moveya",
    "Hollandverse",
    "Hollandvio",
    "Touristsio",
    "Movehaven",
    "Touristya",
    "Goverse",
    "Hollandhut",
    "Flyhut",
    "Touristsquad",
    "Movecog",
    "Goaholic",
    "Gosio",
    "Flyology",
    "Godeck",
    "Goly",
    "Gocouch",
    "Golux",
    "Hollandgenics",
    "Gonetic",
    "Flyorama",
    "Hollandstation",
    "Hollandya",
    "Flyify",
    "Hollandcouch",
    "Gohut",
    "Flyshack",
    "Gostation",
    "Touristology",
    "Flyvio",
    "Hollandprism",
    "Flytap",
    "Movewind",
    "Hollandtap",
    "Hollandnest",
    "Movesy",
    "Hollandhaven",
    "Gofluent",
    "Hollanddo",
    "Touristverse",
    "Flyex",
    "Touristgenics",
    "Touristpad",
    "Touristly",
    "Flystop",
    "Touristporium",
    "Touristhaven",
    "Hollandistic",
    "Touristify",
    "Flygenix",
    "Gojet",
    "Movecouch",
    "Hollandx",
    "Flyegy",
    "Hollandorama",
    "Moveaholic",
    "Gozilla",
    "Movevio",
    "Hollandava",
    "Moveology",
    "Gozoid",
    "Touristshack",
    "Flyfluent",
    "Moveorama",
    "Hollandzilla",
    "Gotap",
    "Touristry",
    "Touristnest",
    "Flyly"
  ));
}
