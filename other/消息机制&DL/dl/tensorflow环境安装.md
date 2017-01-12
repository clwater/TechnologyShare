tensorflow环境安装

详情参阅：[Download and Setup](https://www.tensorflow.org/get_started/os_setup#anaconda_installation)

1. Anaconda installation
2. Create a conda environment called tensorflow:
   
   ```
   # Python 2.7
   $ conda create -n tensorflow python=2.7
   ```    

3. 激活环境与关闭环境
   
   ```
   $ source activate tensorflow
   (tensorflow)$  # Your prompt should change.
   # Run Python programs that use TensorFlow.
     ...
   # When you are done using TensorFlow, deactivate the    environment.
   (tensorflow)$ source deactivate tensorflow
   ``` 
   
4. Using pip install tensorflow.
   
   ```
   # Mac OS X, CPU only, Python 2.7:
   (tensorflow)$ export TF_BINARY_URL=https://storage.googleapis.com/tensorflow/mac/cpu/tensorflow-0.12.1-py2-none-any.whl
   
   # Python 2
   (tensorflow)$ pip install --ignore-installed --upgrade $TF_BINARY_URL
   ```

5. test tensorflow 
   
   ```
$ python
...
>>> import tensorflow as tf
>>> hello = tf.constant('Hello, TensorFlow!')
>>> sess = tf.Session()
>>> print(sess.run(hello))
Hello, TensorFlow!
>>> a = tf.constant(10)
>>> b = tf.constant(32)
>>> print(sess.run(a + b))
42
>>>
   ```  
