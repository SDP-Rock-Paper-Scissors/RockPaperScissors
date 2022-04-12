In this file it is documented, which options have been evaluated for the detection rock paper
and scissors with the camera. And what has been integrated.
## Overview
We have the requirement that the detection of rock paper scissors runs offline on the device.

There are two possibilities to identify if rock paper or scissors is shown. Either image labeling is used
or a pose estimation for the hand. The downside of the pose estimation is that, the pose has to be mapped
to rock, paper or scissors which is not trivial. 

### 1. Image Labeling
This can be implemented using the google [ML kit](https://developers.google.com/ml-kit/vision/image-labeling).

A custom model is trained with the rock paper [scissors dataset](https://www.tensorflow.org/datasets/catalog/rock_paper_scissors)

The EfficentNet-Lite0 model was used to classify the images. An accuracy of 0.9140 was achieved on 
the given test set. 

The results on the app are not that good. The model coud be optimized by increasing the dataset. [This](https://www.kaggle.com/datasets/sanikamal/rock-paper-scissors-dataset) could be added to the dataset mentioned above. 

A problem with both datasets is, that the hands are recorded infront a white surface or a green screen. Therfore the background of the pictures taken with the camera should be remove. This could be done with image segmentation but makes the whole pipline much more complicated.


### 2. Pose estimation

A second approach would be to get the pose of the hand and deduce if a rock, paper or scissor is shown. Media Pipe provoides such a [model](https://google.github.io/mediapipe/solutions/hands), specificly made for mobile usage. The challange for this approach is to map the pose to the three possible symbols. This could either be done with a second NN or hard coded.
