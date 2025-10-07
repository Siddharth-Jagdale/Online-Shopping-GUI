package com.mycompany.onlineshop;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static List<Product> cartItems = new ArrayList<>();

    public static void addToCart(Product p) {
        cartItems.add(p);
    }

    public static List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public static void clearCart() {
        cartItems.clear();
    }
}
