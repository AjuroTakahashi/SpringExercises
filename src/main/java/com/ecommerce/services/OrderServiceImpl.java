package com.ecommerce.services;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderProduct;
import com.ecommerce.model.Product;
import exceptions.StockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service("orders")
public class OrderServiceImpl implements OrderService{

    private ArrayList<Order> orderList = new ArrayList<>();
    @Autowired
    private ProductService productService;

    @Override
    public ArrayList<Order> getAllOrders() {
        return orderList;
    }

    @Override
    public Order create(Order order) {
        order.setStatus("En cours.");
        if (!orderList.contains(order)) {
            orderList.add(order);
        }
        return order;
    }

    @Override
    public void update(Order order) throws StockException {
        if (!Objects.equals(order.getStatus(), "Payée.")) {
            for (OrderProduct op : order.getOrderProducts()) {
                Product product = op.getProduct();
                if (!productService.isProductAvailable(product, op.getQuantity())) {
                    throw new StockException();
                }
            }
            order.setStatus("Payée.");

            for (OrderProduct op : order.getOrderProducts()) {
                Product product = op.getProduct();
                if (productService.isProductAvailable(product, op.getQuantity())) {
                    productService.removeProduct(product, op.getQuantity());
                }
            }
        }
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
