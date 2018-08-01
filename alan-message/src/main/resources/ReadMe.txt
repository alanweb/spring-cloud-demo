此模块为在线计费信息接口解析报文日志
####需求
1、生产日志获取
    消费 kafka Message日志
2、日志的解压及解析
    2.1、规则 oc-front message报文 TradeSession关联oc-dcc message报文的发送到省boss报文Trade-Session
    2.2、获取所需要的值 封装成BossMessage Bean
3、生成规则dat文件并压缩s_20007_SZS_05001_yyyymmdd_XX_XXX.dat.gz文件
    3.1、对处理好的BossMessage 按规则写出 dat.gz 文件
4、校验文件名s_20007_SZS_05001_yyyymmdd_XX.verf
    4.1、按规则生成verf校验文件
5、ftp 每天14:00上传解析的gz文件
    5.1、定时每天14:00 上传 生成好的头一天数据 到ftp服务器
#### 使用
  修改配置 application.yml
  kafka
    bootstrap-servers kafka服务ip地址
    default-topic     topic
  ftp
    tmpPath     临时存放目录
    ip          ftp服务器ip地址
    port        ftp服务器端口
    uploadPath  ftp目录
    sysUser     ftp用户名
    passWord    ftp密码
    cron        定时器规则