# AI Instructions
## 概要
- これはJavaで作った飲食店のスマホオーダーシステムです。
- DBはPostgreSQL
- テーブル定義は以下
  ./DDL/DDL.txt
- 文字コードはUTF-8, 改行コードはCR+LF
## アーキテクチャ
- jsoupを使用してテンプレートのHTMLを編集するという方法でサーバサイドレンダリングする独自のフレームワークを作っています。
- ./src/main/java/com/kmaebashi/sporder以下がスマホオーダーシステムのソースです。
- routerでルーティングして、それをcontrollerで受けて、serviceがビジネスロジック、dbaccessでDBのアクセスを行います。
- テンプレートになるHTMLは、src/main/webapp/WEB-INF/htmltemplate以下にあります。このHTMLは、そのままHTMLとしてふつうに表示できなければいけません。
- htmltempate以下のHTMLをふつうに表示するためcssフォルダもそこに置いていますが、実際に動くのはsrc/main/webapp/css以下のものです。cssを修正する場合はsrc/main/webapp/WEB-INF/htmltemplate/css以下のものをまず修正し、それをsrc/main/webapps/css以下にコピーしてください。
- スクリプトはTypeScriptで作成します。。src/main/webapp/WEB-INF/htmltemplate/ts以下に.tsファイルを配置します。
- TypeScriptファイルを修正したら、以下を実施する必要があります。
  - src/main/webapp/WEB-INF/htmltemplate以下でtscコマンドを実行
  - これでtsフォルダの隣のjsフォルダにJavaScriptが作られます。
  - 実際に動くのはsrc/main/webapp/js以下のものなので、tsc実行後にsrc/main/webapp/WEB-INF/htmltemplate/js以下のJavaScriptをsrc/main/webapp/jsにコピーしてください。
実際に動くのは
- serviceの階層で、取得したデータをもとにHTMLを組み立てます。
- dbaccessでは、1メソッドでひとつのSQLを実行します。selectを行った場合、自作のマッパーでDTOに値を設定します。DTOにはTableColumn属性で列名を指定します(CategoryDto.javaを参照)。
- 自作のマッパーResultSetMapperでは、データを複数件取得した場合はtoDtoList()メソッドでResultSetをDTOのリストに変換します。結果が1件だとわかっているときは、toDto()メソッドを使います。
- controller, service, dbaccessそれぞれの階層で、処理を直接実行するのではなく、引数で受け取ったinvokerのinvoke()メソッドに実行させたい処理のラムダ式を渡しています。これは階層を超える際にフレームワークを挟むことでログを出したり例外をcatchしたりするためです。
- DB更新を含むserviceではServiceInvokerのinvokeにオプション引数でInvokerOption.TRANSACTIONALを付けるとトランザクションが作られます。
- あなたに頼みたいのはservice以下です。router, controllerまでは私が手で書きます。
