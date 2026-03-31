package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * CartItemAttribute Entity
 * Simplified mapping between cart items and their selected product attributes
 * Only tracks which attributes are selected, without quantity or pricing
 */
@Entity
@Table(name = "CART_ITEM_ATTRIBUTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemAttribute {

    private static final Logger logger = LoggerFactory.getLogger(CartItemAttribute.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ITEM_ATTRIBUTE_ID")
    private Long cartItemAttributeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ITEM_ID", nullable = false)
    private CartItem cartItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ATTRIBUTE_ID", nullable = false)
    private ProductAttribute productAttribute;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new CartItemAttribute for cart item ID: {}, product attribute ID: {}", 
                    cartItem != null ? cartItem.getCartItemId() : null,
                    productAttribute != null ? productAttribute.getProductAttributeId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating CartItemAttribute ID: {}", cartItemAttributeId);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Get attribute name from associated product attribute
     * @return Attribute name or null if not loaded
     */
    public String getAttributeName() {
        return (productAttribute != null && productAttribute.getProductAttr() != null) 
            ? productAttribute.getProductAttr().getProductAttrName() 
            : null;
    }

    /**
     * Get attribute value from associated product attribute
     * @return Attribute value or null if not loaded
     */
    public String getAttributeValue() {
        return (productAttribute != null) 
            ? productAttribute.getProductAttributeValue() 
            : null;
    }
}