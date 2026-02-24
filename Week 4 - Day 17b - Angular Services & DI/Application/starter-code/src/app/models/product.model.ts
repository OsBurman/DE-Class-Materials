// product.model.ts — provided, do not modify
export interface Product {
  id: number;
  name: string;
  price: number;
  category: 'Electronics' | 'Clothing' | 'Books' | 'Other';
  description: string;
  imageUrl: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export const PRODUCTS: Product[] = [
  { id: 1, name: 'Wireless Headphones', price: 79.99, category: 'Electronics', description: 'High-quality Bluetooth headphones with 30hr battery.', imageUrl: 'https://via.placeholder.com/200x150?text=Headphones' },
  { id: 2, name: 'Programming T-Shirt', price: 24.99, category: 'Clothing', description: 'Comfortable cotton tee for developers.', imageUrl: 'https://via.placeholder.com/200x150?text=T-Shirt' },
  { id: 3, name: 'Clean Code', price: 34.99, category: 'Books', description: 'A handbook of agile software craftsmanship by Robert C. Martin.', imageUrl: 'https://via.placeholder.com/200x150?text=Book' },
  { id: 4, name: 'USB-C Hub', price: 49.99, category: 'Electronics', description: '7-in-1 multiport adapter for laptops.', imageUrl: 'https://via.placeholder.com/200x150?text=USB+Hub' },
  { id: 5, name: 'Mechanical Keyboard', price: 129.99, category: 'Electronics', description: 'Tactile mechanical switches, RGB backlit.', imageUrl: 'https://via.placeholder.com/200x150?text=Keyboard' },
  { id: 6, name: 'Design Patterns', price: 39.99, category: 'Books', description: 'Elements of reusable object-oriented software.', imageUrl: 'https://via.placeholder.com/200x150?text=Book' },
];
