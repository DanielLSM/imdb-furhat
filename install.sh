 cd ~

 # clone repo
 git clone https://github.com/DanielLSM/imdb-furhat

# move to furhat repo skill
cd imdb-furhat/MovieCritic

echo "Compiling the skill!!" 
sudo ./gradlew shadowJar

# move to python server
cd ../detection-server

wget -O weights.08.hdf5 https://kth.box.com/s/d75f62kllb35eixy1f15txo7aozkmglq

# install conda environment
conda env create -f furhat.yml

conda init bash

conda activate furhat1

echo "You are ready to RUN" 
echo "Start the IPWebcam in your phone and alter AUDIO IP with AUDIO port on launch.json"
echo "Start the server with #python server.py#"
echo "Start the skill! For more instructions go to https://github.com/DanielLSM/imdb-furhat"
