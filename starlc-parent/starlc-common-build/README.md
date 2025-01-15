## common-build
> 第三方依赖包版本管理



## 打包命令说明
> 以nationalization-11.3.0版本为例进行打包处理,如下：
* mvn versions:set-property -DallowSnaphosts=true -Dproperty=revision -DnewVersion=nationalization-11.3.0
* mvn versions:commit
* mvn -U clean deploy -Dmaven.test.skip=true -Drevision=nationalization-11.3.0
> 如果是某个jar包不用修改版本号，单独需要打包：
* cd 切换到该jar包目录，执行如下命令即可
* mvn -U clean deploy -Dmaven.test.skip=true -Drevision=nationalization-11.3.0



## 运行说明
> 说明如何运行和使用你的项目，建议给出具体的步骤说明
* 操作一
* 操作二
* 操作三  



## 测试说明
> 如果有测试相关内容需要说明，请填写在这里  



## 技术架构
> 使用的技术框架或系统架构图等相关说明，请填写在这里  


## 协作者
> 高效的协作会激发无尽的创造力，将他们的名字记录在这里吧
