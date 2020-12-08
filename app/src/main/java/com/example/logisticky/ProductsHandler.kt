package com.example.logisticky

class ProductsHandler {

    companion object {

        fun getProductIdAndName(): ArrayList<ProductItem>{
            val simpleProductsList = ArrayList<ProductItem>()

                //Here should be full fetchJson

                simpleProductsList.add(ProductItem("Product A"))
                simpleProductsList.add(ProductItem("Product B"))
                simpleProductsList.add(ProductItem("Product C"))


            return simpleProductsList
        }
    }
}