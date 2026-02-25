import animals.*;
import behaviors.*;
import specific.*;

public class Main {
    public static void main(String[] args) {

                em.out.println("=== Animal Ki m ===\n");
                   
                ODO Task 5: Polymorphic array nimal[] holds all 4 concrete types
                al[] animals = new Animal[4]  
                nimals[0] = new Dog("Rex", 4, " mestic", "Golden");
                nimals[1] = new Eagle("Bald", 7 "Mountains") // animals[2] = new Duck("Donald", 3, "Wetlands");
        // animals[3] = new Crocodile("Croc", 12, "Swamp");

        // TODO: Loop over the array and call describe() and makeSound() on each
        // Notice: the method called is determined at RUNTIME — this is polymorphism


        // TODO Task 6: Swimmable array — only animals that implement Swimmable
        // Swimmable[] swimmers = { (Swimmable) animals[0], ... };
        // Loop and call swim() on each


        // TODO Task 7: instanceof check and downcast
        // Loop over animals[]:
        //   if (animals[i] instanceof Trainable) {
        //       Trainable t = (Trainable) animals[i];
        //       System.out.println(t.train("sit"));
        // 
        //   }

    }
}

        //  
        // 
        // 