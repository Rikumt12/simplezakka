package com.example.simplezakka.config;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.CategoryRepository;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
 
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import com.example.simplezakka.entity.Category;
 
 
 
 
@Component
public class DataLoader implements CommandLineRunner {
 
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    public DataLoader(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
   
    @Override
    public void run(String... args) {
        loadSampleCategories();  
        loadSampleProducts();    
    }
 
    private void loadSampleCategories() {
        if (categoryRepository.count() > 0) {
            return; 
        }
        List<Category> categories = Arrays.asList(
            createCategory("デスク周辺"),
            createCategory("リビングインテリア"),
            createCategory("キッチン"),
            createCategory("おでかけ"),
            createCategory("バス・トイレ")
        );
        categoryRepository.saveAll(categories);
    }
 
    private Category createCategory(String name) {
        Category category = new Category();
        category.setCategoryName(name);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }
   
   
   
 
    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            return; 
        }
        Category desk = categoryRepository.findByCategoryName("デスク周辺")
                    .orElseThrow(() -> new RuntimeException("Category not found: デスク周辺"));
 
        Category living = categoryRepository.findByCategoryName("リビングインテリア")
                    .orElseThrow(() -> new RuntimeException("Category not found: リビングインテリア"));
 
        Category kitchen = categoryRepository.findByCategoryName("キッチン")
                    .orElseThrow(() -> new RuntimeException("Category not found: キッチン"));
 
        Category outside = categoryRepository.findByCategoryName("おでかけ")
                    .orElseThrow(() -> new RuntimeException("Category not found: おでかけ"));
 
        Category bath = categoryRepository.findByCategoryName("バス・トイレ")
                    .orElseThrow(() -> new RuntimeException("Category not found: バス・トイレ"));
 
        List<Product> products = Arrays.asList(
            createProduct(
                "シンプルデスクオーガナイザー",
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。",
                3500,
                20,
                "/images/desk-organizer.png",
                true,
                desk
            ),
            createProduct(
                "アロマディフューザー（ウッド）",
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。",
                4200,
                15,
                "/images/aroma-diffuser.png",
                true,
                living
            ),
            createProduct(
                "コットンブランケット",
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。",
                5800,
                10,
                "/images/cotton-blanket.png",
                false,
                living
            ),
            createProduct(
                "ステンレスタンブラー",
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。",
                2800,
                30,
                "/images/tumbler.png",
                false,
                kitchen
            ),
            createProduct(
                "ミニマルウォールクロック",
                "余計な装飾のないシンプルな壁掛け時計。静音設計！",
                3200,
                25,
                "/images/wall-clock.png",
                false,
                living
            ),
            createProduct(
                "リネンクッションカバー",
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。",
                2500,
                40,
                "/images/cushion-cover.png",
                true,
                living
            ),
            createProduct(
                "陶器フラワーベース",
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。",
                4000,
                15,
                "/images/flower-vase.png",
                false,
                living
            ),
            createProduct(
                "木製コースター（4枚セット）",
                "天然木を使用したシンプルなデザインのコースター。4枚セット。",
                1800,
                50,
                "/images/wooden-coaster.png",
                false,
                kitchen
            ),
            createProduct(
                "キャンバストートバッグ",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                3600,
                35,
                "/images/tote-bag.png",
                true,
                outside
            ),
             createProduct(
                "ソープディスペンサー",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                1800,
                35,
                "/images/sopedispenser.png",
                true,
                bath
            ),
             createProduct(
                "珪藻土バスマット",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                1500,
                35,
                "/images/keisoudo.png",
                true,
                bath
            ),
             createProduct(
                "ベーシックノート", 
                "書き心地の良い紙を使用したシンプルなノート。日常使いに最適。", 
                500, 
                35, 
                "/images/note.png", 
                true,
                desk      
            ),
            createProduct(
                "ペン立て(木製)",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                2500,
                35,
                 "/images/penntate.png",
                true,
                desk
            ),
             createProduct(
                "カレンダー（2025）",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                900,
                35,
                "/images/karennda-.png",
                true,
                desk
            ),
             createProduct(
                "ウォーターボトル",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                2300,
                35,
                "/images/suitou.png",
                true,
                outside
            ),
             createProduct(
                "折りたたみかさ",
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
                3200,
                35,
                 "/images/kasa.jpg",
                true,
                outside
            ),
            createProduct(
                "ガラス保存容器セット",
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。",
                1300,
                20,
                "/images/glass-container.png",
                false,
                kitchen
            )
        );
       
        productRepository.saveAll(products);
    }
   
    private Product createProduct(String name, String description, Integer price, Integer stock, String imageUrl, Boolean isRecommended,Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setIsRecommended(isRecommended);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}