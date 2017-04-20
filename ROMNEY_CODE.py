import pandas
import nltk
import numpy as np
import re
from string import digits
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
import string
from bs4 import BeautifulSoup
from nltk.stem.porter import *
from sklearn.feature_extraction.text import CountVectorizer
#from sklearn import cross_validation
from sklearn.model_selection import KFold
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn import svm
from sklearn.metrics import classification_report
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import CountVectorizer
#from sklearn.svm import LinearSVC
from sklearn.feature_extraction.text import TfidfTransformer
#from sklearn.multiclass import OneVsRestClassifier
from sklearn.metrics import accuracy_score
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import precision_recall_fscore_support
from sklearn.metrics import precision_recall_fscore_support as score

def tokenize(tweet):
	stemmer = PorterStemmer()
	stop_words = set(stopwords.words("english"))
	tweet = tweet.lower()
	custom_stop_words = set((u'im', u'rt',u'obama',u'president'))
	soup = BeautifulSoup(tweet,'lxml')
	tweet = soup.get_text()
	tweet = re.sub(r"http\S+|https\S+|www.\S+|@\S+|#\S+|\d+|\d+\w+|[^a-z ]"," ", tweet)
	tweet = re.sub(r'(.)\1+', r'\1\1', tweet) 
	tweet = re.sub(r'\w+\d+', '\w+', tweet)
	remove_list = ['im', 'rt','obama','romney']
	#example_words = tweet.split()
	

	
	#print twee
	example_words = word_tokenize(tweet)
	#print example_words
	example_words = filter(lambda x: x not in string.punctuation, example_words)
	cleaned_text = filter(lambda x: x not in stop_words, example_words)
	cleaned_text = filter(lambda x: x not in custom_stop_words, cleaned_text)
	for p in range(len(cleaned_text)):
		cleaned_text[p] = re.sub(r"[^a-z]",' ',cleaned_text[p])
	singular_clean_text = [stemmer.stem(x) for x in cleaned_text]
	tweet = " ".join(singular_clean_text)
	#return singular_clean_text
	#print tweet
	return tweet

def get_words_in_tweets(tweets):
	all_words = []
	for (words, sentiment) in tweets:
		all_words.extend(words)
	#print all_words
	return all_words
	

def get_word_features(wordlist):
	wordlist = nltk.FreqDist(wordlist)
	word_features = wordlist.keys()
	#print '-------'
	#print wordlist
	return word_features
	
def extract_features(document):
	document_words = set(document)
	features = {}
	for word in word_features:
		features['%s' % word] = (word in document_words)
	return features
	
words = []
test_words = []
total_precision_1 = 0
total_recall_1 = 0
total_precision_minus = 0
total_recall_minus = 0
accuracy_total = 0
total_fscore_pos = 0
total_fscore_neg = 0
#vector_function = CountVectorizer(analyzer = "word", tokenizer = None, preprocessor = None,stop_words = None, max_features = 9000)
xl = pandas.ExcelFile("tweet.xlsx")
tl = pandas.ExcelFile("test_tweet.xlsx")
#print tl.sheet_names
obama_sheet = pandas.read_excel(xl,"Obama",parse_cols=[3,4])
obama_test = pandas.read_excel(tl,"Obama",parse_cols=[0,4])
romney_sheet = pandas.read_excel(xl,"Romney",parse_cols=[3,4])
romney_test = pandas.read_excel(tl,"Romney",parse_cols=[0,4])
#print obama_test
for index,row  in romney_sheet.iterrows():
	class_row = row['Class']
	if class_row == 1 or class_row == -1 or class_row == 0:
		if not pandas.isnull(row['Anootated tweet']):
			words.append(((tokenize(row['Anootated tweet']),row['Class'])))
for index1,row1  in romney_test.iterrows():
	class_row1 = row1['Class']
	if class_row1 == 1 or class_row1 == -1 or class_row1 == 0:
		if not pandas.isnull(row1['Anootated tweet']):
			test_words.append(((tokenize(row1['Anootated tweet']),row1['Class'])))
			#print test_words
#for k in words:
#	print k[0]
#print test_words
#xp = 0
#kf = KFold(n_splits=10)
#for train, test in words:
	#print train
#TP1 = 0
#TPminus1 = 0
#FN1 = 0
#FNminus1 = 0
#TN1 = 0
#TNminus1 = 0
#FP1 = 0
#FPminus1 = 0
test_tweet = []
training_data = []
training_class = []
testing_data = []
testing_tweet = []
testing_class = []
for j in words:
		#for l in words[j]:
		#print type(l)
	training_data.append(j[0])
	training_class.append(j[1])
#print training_data
#print training_class
for h in test_words:
		#for a in words[h]:
	testing_tweet.append(h[0])
	testing_class.append(h[1])
		#testing_data.append(words[h])
#print testing_tweet
#print testing_class
	#for i in testing_data:
		#test_tweet.append(tuple(i[0]))
	#print test_tweet
	
	#svm.SVC(kernel = 'linear')
	# MultinomialNB()
	#LogisticRegression()
	#DecisionTreeClassifier()
	
classifier = Pipeline([('vectorizer', CountVectorizer()),('tfidf', TfidfTransformer()),('clf', LogisticRegression())])
classifier.fit(training_data, training_class)
predicted = classifier.predict(testing_tweet)
report = classification_report(testing_class, predicted)
accuracy = accuracy_score(testing_class, predicted)
#print(report)
#print(accuracy)
precision, recall, fscore, support = score(testing_class,predicted)

print('Precision:')
print('     Positive class = ',precision[2])
print('     Negative class = ', precision[0])
print('Recall:')
print('     Positive class = ',recall[2])
print('     Negative class = ', recall[0])
print('fscore:')
print('     Positive class = ',fscore[2])
print('     Negative class = ', fscore[0])
print('Accuracy: ', accuracy)

	#print predicted 
	#print testing_class
	#s =  classification_report(testing_class, predicted)
	#print s
	#print precision_recall_fscore_support(testing_class, predicted, average='weighted')
	
	#print xp	
#print xp/10
#for label,observed in zip(testing_class, predicted):
		
		#print "observed", observed
		#print "label", label
		
#	if observed == 1 and label == 1:
#		TP1 = TP1 + 1
			#print observed, "0xxTP1xxl", label
			
#	if observed == -1 and label == 1:
#		FN1 = FN1 + 1
			#print observed, "0xxFN1xxl", label
			
#	if observed == 0 and label == 1:
#		FN1 = FN1 + 1
	#		#print observed, "0xxFN1xxl", label
			
        
#	if observed == 1 and label == -1:
#		FP1 = FP1 + 1
			#print observed, "0xxFP1xxl", label
			
#	if observed == 1 and label == 0:
#		FP1 = FP1 + 1
			#print observed, "0xxFP1xxl", label
			
#	if observed == -1 and label == -1:
#		TPminus1 = TPminus1 + 1
			#print observed, "0xxTPminusxxl", label
			
#	if observed == 1 and label == -1:
#		FNminus1 = FNminus1 + 1
#			#print observed, "0xxFNminusxxl", label
		
#	if observed == 0 and label == -1:
#		FNminus1 = FNminus1 + 1
			#print observed, "0xxFNminusxxl", label
			 
#	if observed == -1 and label == 1:
#		FPminus1 = FPminus1 + 1
			#print observed, "0xxFPminusxxl", label
			
#	if observed == -1 and label == 0:
#		FPminus1 = FPminus1 + 1
			#print observed, "0xxFPminusxxl", label
			
	
#	p1 = TP1/float(TP1 + FP1)
#	print "+ve p", p1
#	r1 = TP1/float(TP1 + FN1)
#	print "+ve r", r1
#	F_score_1 = (2*p1*r1)/(p1+r1)
#	print "fscore_+ve:",F_score_1
#	pminus1 = TPminus1/float(TPminus1 + FPminus1)
#	rminus1 = TPminus1/float(TPminus1 + FNminus1)
#	print "-ve r", rminus1
#	F_score_minus = (2*pminus1*rminus1)/(pminus1+rminus1)
#	print "fscore_-ve:",F_score_minus
#	print accuracy_score(testing_class, predicted)
#	xp = xp + accuracy_score(testing_class, predicted)
	#accuracy = nltk.classify.accuracy(classifier, testing_set)
	#accuracy_total = accuracy_total + accuracy
#	total_precision_1 = total_precision_1 + p1
#	total_recall_1 = total_recall_1 + r1
#	total_precision_minus = total_precision_minus + pminus1
#	total_recall_minus = total_recall_minus + rminus1
#	total_fscore_pos = total_fscore_pos + F_score_1
#	total_fscore_neg = total_fscore_neg + F_score_minus 
#	print '----------------------------------------------------------------------------------'
#print "ROMNEY TWEETS"
#print "overall accuracy:",xp/10
#print "overall precision for positive class:", total_precision_1/10
#print "overall recall for positive class:", total_recall_1/10
#print "overall precision for negitive class:", total_precision_minus/10
#print "overall recall for negitive class:", total_recall_minus/10
#print "overall F-Score for Positive class:",total_fscore_pos/10
#print "overall F-Score for Negitive class:",total_fscore_neg/10