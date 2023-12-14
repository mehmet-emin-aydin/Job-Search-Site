#Proje Kapsami
Veritabanı Lab 2324 - Proje Duyurusu
Gruplarınıza atanan konu kapsamında aşağıdaki özellikleri dikkate alarak, arayüzü olan, veritabanına bağlantı sağlanan bir uygulama gerçeklemeniz istenmektedir. Kullanacağınız veritabanı PostgreSQL olmalıdır. Uygulamanız için platform kısıtı yoktur, istediğiniz dili ve geliştirme ortamını kullanabilirsiniz. Tabloların oluşturulması ve verilerin girilmesi işlemi size aittir.  Gerçekleştirilecek veritabanı sistemi ve uygulaması için istenenler şunlardır:

Oluşturacağınız veritabanı en az 4 tablo içermelidir. Her tabloda en az 10 kayıt bulunmalıdır.
Tablolarınızda primary key ve foreign key kısıtlarını kullanmalısınız.
En az bir tabloda silme kısıtı ve sayı kısıtı olmalıdır.
Arayüzden en az birer tane insert, update ve delete işlemi gerçekleştirilebilmelidir.
Arayüzden girilecek bir değere göre ekrana sonuçların listelendiği bir sorgu yazmalısınız.
Arayüzden çağrılan sorgulardan en az biri “view” olarak tanımlanmış olmalıdır.
En az bir adet “sequence” oluşturmalı ve arayüzden yapılacak insert sırasında ilgili sütundaki değerlerin otomatik olarak atanmasını sağlamalısınız.
Arayüzden çağrılan sorgulardan en az birinde union veya intersect veya except kullanmış olmalısınız.
Sorgularınızın en az biri aggregate fonksiyonlar içermeli, having ifadesi kullanılmalıdır.
Arayüzden girilen değerleri parametre olarak alıp ekrana sonuç döndüren 3 farklı SQL fonksiyonu tanımlamış olmalısınız. Bu fonksiyonların en az birinde “record” ve “cursor” tanımı-kullanımı olmalıdır. 
2 adet trigger tanımlamalı ve arayüzden girilecek değerlerle tetiklemelisiniz. Trigger’ın çalıştığına dair arayüze bilgilendirme mesajı döndürülmelidir.

Projenizle beraber bir de rapor yazmanız istenmektedir. Raporda bulunması gerekenler:
Tasarlanan veri tabanına ait ER diyagramı
Tablolarınızın ekran görüntüleri
Yukarıdaki maddelerin sağlandığını gösteren kod blokları. Örneğin, yukarıdaki 4.maddede geçen “Arayüzden en az birer tane insert, update ve delete işlemi gerçekleştirilebilmelidir” için;
     			INSERT INTO …..
UPDATE …...
			(yazdığınız sql kodları)

Proje tesliminde göndereceğiniz zip dosyasında bulunması gerekenler:
Proje kodları
.sql uzantılı veritabanı şema dosyanız (tablo oluşturma kodlarının bulunduğu dosya)
Proje raporunuz (pdf formatında)

Son Teslim Tarihi :  05.01.2024 Cuma 23:59’a kadar, ileride linki ilan edilecek forma yüklemelerinizi yapmanız istenecektir. Projelerinizi 6-7 Ocak tarihlerinde, gün içerisinde verilecek randevu saatinizde, 10 dk’lık süre içerisinde ZOOM üzerinden sunmanız beklenecektir. 

Projeden puan alınabilmesi için sunum yapılması şarttır. Puanlama kriterleri şöyledir:
Tasarım (ER Diyagramı) : 15 puan
Sorgular & Fonksiyonlar & Triggerlar : 60 puan
Arayüz tasarımı ve sistemin kullanılabilirliği : 25 puan

Projeden alacağınız puan, dersin genel notuna %10 etki edecektir. 




#İşBul - İş Başvuru Sistemi
Kullanıcı, sisteme kullanıcı adı ve şifre ile kayıt olup, login ekranından giriş yapar. Filtreleme veya arama yaparak kendisine uygun ilanların listelenmesini sağlar. Kendi hesap sayfasından çeşitli kişisel bilgilerini girip güncelleyebilmelidir (iş tecrübesi, okul vs.). Başvurduğu ilanlar kullanıcı sayfasında listelenmelidir. İlanların açılış tarihi ve başvuran sayıları ekranda görüntülenmeli, kullanıcı başvurduktan sonra başvuran sayısı güncellenmelidir. Ek özellikler olarak; çeşitli şirketlerin sertifika programları olmaktadır. Kullanıcılar bu kurslara da başvurabilir. 

