# 教室予約アプリ

Spring Boot + Thymeleaf + Spring Security + MySQL で構築した教室予約システム

## ロール
- 管理者 (ROLE_ADMIN): 教室の登録・削除
- 講師 (ROLE_TEACHER): 教室を選んで授業（予約枠）を作成
- 生徒 (ROLE_STUDENT): 公開されている授業に予約申込み

## セットアップ手順

### 1. MySQLでデータベースを作成

```sql
CREATE DATABASE classroom_reservation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'classroom_user'@'localhost' IDENTIFIED BY 'classroom_password';
GRANT ALL PRIVILEGES ON classroom_reservation_db.* TO 'classroom_user'@'localhost';
FLUSH PRIVILEGES;
```

(setup.sql に同じ内容があります)

### 2. application.properties を必要に応じて編集

`src/main/resources/application.properties` のDB接続情報（ユーザー名・パスワード）を環境に合わせて変更してください。

### 3. ビルド・起動

```bash
mvn spring-boot:run
```

起動後 http://localhost:8080/login にアクセスしてください。

## 使い方の流れ

1. ログイン画面の「管理者として登録」からまず管理者アカウントを作成しログイン
2. 管理者で教室を登録（教室名・定員・場所）
3. 「講師として登録」から講師アカウントを作成しログイン、教室を選んで授業（日付・時間・授業名・定員）を作成
4. 「生徒として登録」から生徒アカウントを作成しログイン、公開されている授業一覧から予約

## 主な機能・チェック仕様

- パスワードはBCryptでハッシュ化して保存
- 同一教室・同一日付・時間帯が重複する予約は作成不可
- 定員に達した授業への予約は不可
- 同じ授業への二重予約は不可
- 講師は自分が作成した予約のみ削除可能
