# Trang web tuyển dụng tích hợp AI

## Giới thiệu

Dự án *Trang web tuyển dụng tích hợp AI* là một giải pháp công nghệ giúp ứng viên kiểm tra độ tương đồng giữa CV và mô tả công việc (JD), đồng thời hỗ trợ nhà tuyển dụng quản lý JD một cách hiệu quả. Hệ thống cung cấp hai phiên bản so khớp:
- **Phiên bản không AI**: Sử dụng thuật toán so khớp từ khóa để tính điểm tương đồng dựa trên sự xuất hiện của từ trong CV và JD.
- **Phiên bản có AI**: Tích hợp mô hình **Word2Vec** từ **DeepLearning4J** để phân tích ngữ nghĩa, nhận diện các từ đồng nghĩa (như "developer" và "programmer"), nâng độ chính xác từ 70% lên khoảng 85%.

Dự án được xây dựng với:
- **Frontend**: ReactJS, tạo giao diện thân thiện, responsive.
- **Backend**: Spring Boot, sử dụng kiến trúc RESTful API với các tầng Model, Repository, Service, Controller.
- **Database**: MySQL (hoặc H2 cho phát triển).
- **Công nghệ AI**: DeepLearning4J để tích hợp Word2Vec.

### Tính năng chính
- **Đăng nhập/Đăng ký**: Người dùng (ứng viên và nhà tuyển dụng) tạo tài khoản và đăng nhập.
- **Upload CV**: Ứng viên tải CV dạng PDF, hệ thống tự động trích xuất văn bản.
- **Nhập JD**: Người dùng nhập JD với tiêu đề, mô tả, và từ khóa.
- **Tính độ tương đồng**:
  - Phiên bản không AI: So khớp từ khóa, trả về phần trăm tương đồng và danh sách từ khóa khớp.
  - Phiên bản có AI: Phân tích ngữ nghĩa với Word2Vec, trả về kết quả chính xác hơn.
- **Hiển thị kết quả**: Giao diện hiển thị phần trăm tương đồng, từ khóa khớp, và nội dung CV/JD.
- **Quản lý JD qua email**: Nhà tuyển dụng thêm, sửa, xóa JD bằng cách gửi email.
- **Lưu trữ lịch sử**: Lưu và xem lại kết quả so khớp.

### Giá trị
- **Ứng viên**: Tự đánh giá và cải thiện CV, tăng cơ hội được chọn.
- **Nhà tuyển dụng**: Tiết kiệm thời gian sàng lọc, quản lý JD linh hoạt.
- **Xã hội**: Góp phần nâng cao hiệu quả kết nối lao động.

---

## Cài đặt

### Yêu cầu
- **Frontend**:
  - Node.js (16.x hoặc mới hơn) và npm.
  - Trình duyệt (Chrome, Firefox).
  - IDE: VS Code (khuyến nghị).
- **Backend**:
  - JDK (11 hoặc 17).
  - Maven.
  - MySQL (hoặc H2 cho phát triển).
  - IDE: IntelliJ IDEA (khuyến nghị).
  - Word2Vec model (`GoogleNews-vectors-negative300.bin`) cho phiên bản AI.
- **Phần cứng**: RAM tối thiểu 8GB (khuyến nghị 16GB cho AI).
- **Công cụ**: Git, Postman (kiểm tra API).

---

### Cài đặt Frontend (ReactJS)

#### Bước 1: Cài đặt Node.js và npm
1. Tải Node.js (LTS) từ [https://nodejs.org/](https://nodejs.org/).
2. Cài đặt và kiểm tra:
   ```bash
   node -v
   npm -v
   ```

#### Bước 2: Clone mã nguồn
1. Clone repository:
   ```bash
   git clone https://github.com/your-username/your-repo-name.git
   cd your-repo-name/frontend
   ```

#### Bước 3: Cài đặt dependency
1. Chạy lệnh:
   ```bash
   npm install
   ```
   - Các thư viện chính: `react`, `axios`, `react-router-dom`, `material-ui`.

#### Bước 4: Cấu hình biến môi trường
1. Tạo file `.env` từ `.env.example`:
   ```bash
   cp .env.example .env
   ```
2. Cập nhật URL backend:
   ```env
   REACT_APP_API_URL=http://localhost:8080/api
   ```

#### Bước 5: Chạy ứng dụng
1. Chạy lệnh:
   ```bash
   npm start
   ```
2. Truy cập `http://localhost:3000` trên trình duyệt.

#### Khắc phục sự cố
- **Lỗi "Module not found"**: Xóa `node_modules` và `package-lock.json`, sau đó chạy lại `npm install`.
- **Lỗi API**: Đảm bảo backend chạy trên `http://localhost:8080`.

---

### Cài đặt Backend (Spring Boot)

#### Bước 1: Cài đặt JDK và Maven
1. Tải JDK (11 hoặc 17) từ [https://adoptium.net/](https://adoptium.net/).
2. Cấu hình `JAVA_HOME` và kiểm tra:
   ```bash
   java -version
   ```
3. Tải Maven từ [https://maven.apache.org/](https://maven.apache.org/).
4. Kiểm tra:
   ```bash
   mvn -version
   ```

#### Bước 2: Clone mã nguồn
1. Clone repository:
   ```bash
   git clone https://github.com/your-username/your-repo-name.git
   cd your-repo-name/backend
   ```

#### Bước 3: Cài đặt dependency
1. Mở `pom.xml` và kiểm tra các dependency:
   - Phiên bản không AI: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `pdfbox`, `h2` (hoặc `mysql-connector-java`).
   - Phiên bản có AI: Thêm `deeplearning4j-core`, `deeplearning4j-nlp`, `nd4j-native-platform`.
2. Chạy:
   ```bash
   mvn clean install
   ```

#### Bước 4: Cấu hình mô hình Word2Vec (chỉ cho phiên bản AI)
1. Tải `GoogleNews-vectors-negative300.bin` từ [https://code.google.com/archive/p/word2vec/](https://code.google.com/archive/p/word2vec/).
2. Đặt file vào `src/main/resources/models/`:
   ```bash
   mkdir -p src/main/resources/models
   mv path/to/GoogleNews-vectors-negative300.bin src/main/resources/models/
   ```
3. Cập nhật đường dẫn trong `MatchService.java`:
   ```java
   File modelFile = new File("src/main/resources/models/GoogleNews-vectors-negative300.bin");
   ```

#### Bước 5: Cấu hình cơ sở dữ liệu
1. **H2 Database** (phát triển):
   - Trong `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:h2:mem:testdb
     spring.datasource.driverClassName=org.h2.Driver
     spring.datasource.username=sa
     spring.datasource.password=
     spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
     spring.h2.console.enabled=true
     ```
2. **MySQL** (tùy chọn):
   - Tạo database:
     ```sql
     CREATE DATABASE recruitment_db;
     ```
   - Cập nhật `application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/recruitment_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
     ```

#### Bước 6: Cấu hình email (nếu dùng)
1. Cập nhật `application.properties`:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

#### Bước 7: Chạy ứng dụng
1. Chạy bằng Maven:
   ```bash
   mvn spring-boot:run
   ```
2. Hoặc dùng IDE (IntelliJ IDEA): Mở `Application.java` và nhấn **Run**.
3. Truy cập `http://localhost:8080/h2-console` (nếu dùng H2) hoặc kiểm tra API qua Postman.

#### Bước 8: Kiểm tra API
1. Dùng Postman để kiểm tra:
   - **POST** `/api/match`:
     ```json
     {
         "cvBase64": "data:application/pdf;base64,...",
         "title": "Software Engineer",
         "description": "Develop web applications",
         "keywords": "Java, Spring, React"
     }
     ```
   - **GET** `/api/jobs`: Lấy danh sách JD.

#### Khắc phục sự cố
- **Lỗi "OutOfMemoryError" (AI)**: Tăng bộ nhớ:
  ```bash
  export MAVEN_OPTS="-Xmx4g"
  ```
- **Lỗi "Model file not found"**: Kiểm tra đường dẫn Word2Vec.
- **Lỗi database**: Xác minh cấu hình `spring.datasource`.
- **Lỗi API**: Đảm bảo CORS cho phép `http://localhost:3000`.

---

## Hướng phát triển
- Tích hợp mô hình AI hiện đại hơn (BERT, Sentence Transformers).
- Thêm gợi ý cải thiện CV và chatbot tư vấn.
- Hỗ trợ đa định dạng CV (DOCX, TXT) và tiếng Việt.
- Triển khai trên cloud (AWS, Heroku).

## Liên hệ
- Tác giả: Phạm Tấn Đạt
- Email: [your-email@example.com](mailto:your-email@example.com)
- GitHub: [your-username](https://github.com/your-username)

---