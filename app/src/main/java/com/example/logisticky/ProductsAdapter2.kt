package com.example.logisticky



import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView


class ProductsAdapter2(private var exampleList: List<ProductItem2>):
    RecyclerView.Adapter<ProductsAdapter2.ProductsViewHolder2>() {




    //Represents a single row in list
    class ProductsViewHolder2(itemView: View):RecyclerView.ViewHolder(itemView){
        val textView1: TextView = itemView.findViewById(R.id.text_view_list1)
        val textView2: TextView = itemView.findViewById(R.id.text_view_list2)
        var checkBox: CheckBox = itemView.findViewById(R.id.productCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder2 {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_list_item,
            parent,
            false
        )

        return ProductsViewHolder2(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder2, position: Int) {
        val currentItem = exampleList[position]

        holder.textView1.text = currentItem.text1
        holder.textView2.text = currentItem.text2
        holder.checkBox = currentItem.checkBox

        holder.itemView.setOnClickListener{
            var gName:String = currentItem.text1
            var navController = Navigation.findNavController(it)



            if(!TextUtils.isEmpty(currentItem.text1)){
                val bundle = bundleOf("productId" to currentItem.text1)
                navController!!.navigate(
                    R.id.action_productsFragment_to_productInfoFragment,
                    bundle
                )
            }


        }
    }

    override fun getItemCount() = exampleList.size


}