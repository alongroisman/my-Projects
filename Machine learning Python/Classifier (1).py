from os import listdir
from os.path import join
from nltk.tokenize import word_tokenize
import sys


'''---------------------------Training Functions-------------------------------'''


# Receives classification 'tag'.
# Iterates over words in all files
# of the 'tag' sub-folder.
# Updates the 'words' data structure.
def getWordsFromFiles(tag):
    global words, train_dir
    tag_dir = train_dir + '/' + tag #train_dir
    # Iterates over files in the 'tag' directory:
    for a_file in listdir(tag_dir):
        # extracts content from each file.
        try:
            file_content = word_tokenize(open(join(tag_dir, a_file)).read())
        except UnicodeDecodeError:
            pass
        # Goes over all words in file.
        for word in file_content:
            w = word.lower()
            # if word is already in 'words' - add 1 to it's value.
            if w in words[tag]:
                words[tag][w] += 1
            # Otherwise add the word and initialize it's
            # value to 2 (for naive bayes calculation reasons).
            else:
                words[tag][w] = 2


# Sends each classification(tag) to be trained.
def trainData():
    global tags
    for tag in tags:
        print('training', tag, 'classification.')
        getWordsFromFiles(tag)

'''----------------------------------------------------------------------------'''


'''--------------------------Testing Functions---------------------------------'''


# Calculates the confusion matrix of the test:
def checkBinaryClassification(predicted_classification, real_classification):
    global false_positive, true_negative, false_negative, true_positive, tags

    if 'p' is predicted_classification[0]:
        if predicted_classification is real_classification:
            true_positive += 1
        else:
            false_negative += 1
    #  predicted_classification is 'negative':
    else:
        if predicted_classification is real_classification:
            true_negative += 1
        else:
            false_positive += 1


# Counts how many Classifications were correct/incorrect.
def checkClassification(predicted_classifcation, real_classifcation):
    global correct_classification, wrong_classification

    if predicted_classifcation is real_classifcation:
        correct_classification += 1
    else:
        wrong_classification += 1


# Receives content of a file. Calculates the probability
# for the content to be in each of the classification.
# Return the classification with the highest probability.
def classifyFile(file_content):
    global tags, num_of_files, num_of_all_files, words
    # probability dictionary: key  - tag,
    # value - probability for that tag.
    p_tag = {}
    # We calculate the probability for each classification(tag).
    for tag in tags:
        # The probability for this 'tag' takes into consideration
        # the number of files in this 'tag' divided by total number of files.
        p_tag[tag] = num_of_files[tag] / num_of_all_files

        # We go over all the words in the given content:
        for word in file_content:
            w = word.lower()
            # Multiply the probability for the specific
            # tag, by the amount of times the word appeared
            # in training for that tag( +1 for calculation reasons).
            if w in words[tag]:
                p_tag[tag] *= (words[tag][w] / (num_of_files[tag] + 1))
            else:
                p_tag[tag] *= (1 / (num_of_files[tag] + 1))

    # Returning the tag with the highest probability:
    return max(p_tag, key=p_tag.get)


# Receives a directory(of a specific classification(tag).
# Calculates the classification of each file.
# Checks if Calculation was correct.
def tagTest(tag):
    global test_dir
    tag_dir = test_dir + '/' + tag
    for a_file in listdir(tag_dir):
        # extracts content from each file.
        try:
            file_content = word_tokenize(open(join(tag_dir, a_file)).read())
        except UnicodeDecodeError:
            pass
        # Gets the predicted the classification for the content.
        predicted_classification = classifyFile(file_content)
        if binary_classifier:
            checkBinaryClassification(predicted_classification, tag)
        else:
            checkClassification(predicted_classification, tag)


# Sends each folder in test directory to be tested.
def testData():
    global tags
    for tag in tags:
        print('testing', tag, 'classification.')
        tagTest(tag)

'''----------------------------------------------------------------------------'''


'''--------------------------Other Functions-----------------------------------'''


def calculateBinaryResults():
    global true_positive, false_positive, true_negative, false_negative
    print('true_positive: ', true_positive)
    print('false_positive: ', false_positive)
    print('true_negative: ', true_negative)
    print('false_negative: ', false_negative)

    recall = true_positive / (true_positive + false_negative)
    precision = true_positive / (true_positive + false_positive)
    f_measure = (2 * precision * recall) / (precision + recall)
    accuracy = (true_positive + true_negative) / (false_positive + false_negative + true_positive + true_negative)
    print('Accuracy: ', accuracy)
    print("Recall: ", recall)
    print("Precision: ", precision)
    print("F-Measure: ", f_measure)


def calculateResults():
    global correct_classification, wrong_classification
    print('Amount of correct classifications: ', correct_classification)
    print('Amount of wrong classifications: ', wrong_classification)

    accuracy = correct_classification / (correct_classification + wrong_classification)
    print('Accuracy: ', accuracy)


# Trains and test the given data according
# to naive bayes algorithm.
def naiveBayes():
    global binary_classifier
    print('started training stage:')
    trainData()
    print('\nstarted testing stage:')
    testData()
    print('\nResults:')
    if binary_classifier:
        calculateBinaryResults()
    else:
        calculateResults()


def SetVariables():
    global train_dir, test_dir
    global correct_classification, wrong_classification
    global false_positive, true_negative, false_negative, true_positive
    global tags, num_of_files, words, num_of_all_files, binary_classifier

    # train_dir = 'C:/Users/akiva/OneDrive/Documents/לימודים/Year 3/למידה עמוקה/מטלה 1/my testing data/sentiment/train'
    # test_dir = 'C:/Users/akiva/OneDrive/Documents/לימודים/Year 3/למידה עמוקה/מטלה 1/my testing data/sentiment/test'

    if len(sys.argv) < 3:
        print('No directory arguments were passed to the program.')
        print('Please See README.txt .')
        exit()

    train_dir = sys.argv[1]
    test_dir = sys.argv[2]

    tags = []
    num_of_files = {}  # {tag_1: num_1,..., tag_n: num_n}
    num_of_all_files = 0  # sum amount of files of training data.
    words = {}  # { tag_1:{w_1:count,..., w_k:count},..., tag_n:{w_1:count,..., w_n:count} }
    for sub_folder in listdir(train_dir):
        words[sub_folder] = {}
        tags.append(sub_folder)
        num_of_files[sub_folder] = len(listdir(train_dir + '/' + sub_folder))
        num_of_all_files += len(listdir(train_dir + '/' + sub_folder))

    if len(tags) is 2:
        binary_classifier = True
    else:
        binary_classifier = False

    correct_classification, wrong_classification = 0, 0
    false_positive, true_negative, false_negative, true_positive = 0, 0, 0, 0

'''----------------------------------------------------------------------------'''


global train_dir, test_dir
global correct_classification, wrong_classification
global false_positive, true_negative, false_negative, true_positive
global tags, num_of_files, words, num_of_all_files, binary_classifier

SetVariables()

naiveBayes()



