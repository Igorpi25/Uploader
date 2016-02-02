# Uploader
==========

Андроид библиотека для отправки фотографий через HTTP POST на сервер. Реализованы диалоги для выбора фотографии из галереи, подтверждение и предварительный просмотр. Также проверка доступности интернета, диалоги ошибки сети.


Пример использования:
---------------------

Код использования библиотеки:

```java

import com.android.volley.toolbox.Volley;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.PhotoMultipartRequest;
import com.ivanov.tech.uploader.Uploader;

...

//Запуск протокола выбора и отправки фотографии на сервер
Uploader.protocolChooseAndUpload( getActivity(), getFragmentManager(), mParams,	new Uploader.UploadListener(){

		@Override
		public void onUploaded() {
				//Фотография была выбрана и отправлена на сервер
				...
		}
						
		@Override
		public void onCancelled() {
				//Отмена
		}
			
});
	
```
**Аргументы:**

* `getActivity` - контекст активити
* `getFragmentManager` - `supportFragmentManager` из actionbarsherlock (Внимание! Не путайте с нативным getFragmentManager)
* `R.id.main_container` - передается layout используемый в качестве окна активити, тогда диалог будет показан на весь экран. Если передать другой layout, то в качестве окна диалога будет использован переданный вами layout
* `mParams` - Параметры HTTP POST запроса:
  ```java
  private static final String FILE_PART_NAME = "image";//Метка нужная на сервере
  private static final int[]  SIZE= {600,600};//Размер отправляемого по HTTP POST изображения
  private static final String URL_POST_AVATAR_UPLOAD="http://yourserver.com/v1/avatars/upload";//Url скрипта на сервере
  
  //Параметры HTTP POST запроса
  private static final PhotoMultipartRequest.Params mParams = new Params()  {
	    				
	  @Override
  	public String getPartName() {
  	  return FILE_PART_NAME;					
	  }
	
	  @Override
  	public int[] getSize() {
  	  return SIZE;
	  }
	
	  @Override
  	public String getUrl() {
  	  return URL_POST_AVATAR_UPLOAD;
	  }	    		

  }
  ```
* `UploadListener` - реализация интерфэйса `ProtocolListener` для обратного вызова. Поведение в случае завершения, или отмены протокола

**Описание:**

При вызове `protocolChooseAndUpload` программа создает фрагмент `FragmentChooser`. Вы можете выбрать из галереи, либо перейти на камеру чтобы сделать новое фото:

<img src="screenshot_FragmentChooser.png" width="200">

в случае нажатия кнопки "Gallery" , выйдет стандартная форма для выбора фотографии из галереи. После выбора нужной фотографии, будет показан фрагмент `FragmentPreview` для предварительного просмотра перед отправкой на сервер:

<img src="screenshot_FragmentPreview.png" width="200">

Если нажать на кнопку "Done" фотография отправится на сервер. По завершению будет вызван метод `onUploaded` переданного объекта `UploadListener`. Если нажать "Back", протокол будет прерван и вызовется метод `onCanceled` переданного `UploadListener` объекта.

Используемые библиотеки
-----------------------

* [ActionBarSherlock][1]
* [Volley][2]
* [Glid][9] - используется в демо проекте
* [httpclientandroidlib][10] - для отправки изображения на сервер через Multipart HTTP POST Request
* [Connection][3]- библиотека автора. Диалоги ошибки соединения к интернету. Включен в составе Session
* [Session][8] - библиотека автора. Авторизация пользователя на сервере


[1]: http://actionbarsherlock.com/
[2]: https://github.com/mcxiaoke/android-volley
[3]: https://github.com/Igorpi25/Connection
[4]: https://github.com/Igorpi25/Server
[5]: https://git-scm.com/book/en/v2/Git-Tools-Submodules/
[6]: https://github.com/Igorpi25/Profile
[7]: http://www.androidhive.info/2014/01/how-to-create-rest-api-for-android-app-using-php-slim-and-mysql-day-12-2/

[8]: https://github.com/Igorpi25/Session
[9]: https://github.com/bumptech/glide
[10]:https://code.google.com/archive/p/httpclientandroidlib/
