package com.casc.pmtools.bean;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private List<ProductInfo> productinfo = new ArrayList<>();

    public List<String> getProducts() {
        List<String> products = new ArrayList<>();
        if (productinfo != null) {
            for (ProductInfo product : productinfo) {
                products.add(product.getName());
            }
        }
        return products;
    }

    public String getProductNameByCode(int code) {
        for (ProductInfo info : productinfo) {
            if (info.getCode() == code) {
                return info.getName();
            }
        }
        return null;
    }

    public int getProductCodeByName(String name) {
        for (ProductInfo info : productinfo) {
            if (info.getName().equals(name)) {
                return info.getCode();
            }
        }
        return -1;
    }

    private class ProductInfo {

        private String productname;

        private int productcode;

        public int getCode() {
            return productcode;
        }

        public String getName() {
            return productname;
        }

        @Override
        public String toString() {
            return productname;
        }
    }
}
