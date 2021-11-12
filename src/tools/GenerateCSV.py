import random
import datetime

categories = ["Food","Transportation","Home & Utilities","Others","Entertainment","Personal & Family Care"]

food_items = ["apples","oranges","coffee","lunch","breakfast","dinner","pie","groceries"]
transportation_items = ["car","bus fare","bike repairs","oil change","roller skates","new car","wiper fluid"]
home_items = ["soap","gas bill","electricity bill","water bill","toilet paper","paper towels","sponges","pillow","blanket"]
entertainment_items = ["movie tickets","concert tickets","jetski rental","sky diving","circus tickets","new gaming pc","six flags pass"]
personal_items = ["diapers","make up","hair brush","shampoo","conditioner","razors","band aids","school supplies"]
others_items = ["elephant","space ship","a crane","bulldozer","flying monkey","ninja stars","world hunger","an army of acrobats","the fast food chain wendys"]

transactions = ["date,item,price,category,sign"]

numTransactions = 100
numPosCharges = 20
timeFrame = 365

start_date = datetime.date.today() - datetime.timedelta(days=timeFrame)
end_date = datetime.date.today()
header = "date,item,price,category,sign"



for i in range(numTransactions):
    transaction = []
    random_number_of_days = random.randrange(timeFrame)
    random_date = start_date + datetime.timedelta(days=random_number_of_days)
    
    transaction.append(str(random_date.year) + "-" + str(random_date.month).zfill(2) + "-" + str(random_date.day).zfill(2) + "T" + str(random.randint(0,23)).zfill(2) + ":" + str(random.randint(0,59)).zfill(2) + ":" + str(random.randint(0,59)).zfill(2))

    if (not(i < numPosCharges)):
        category = categories[random.randint(0,5)]

        if category == "Food":
            item = food_items[random.randint(0,len(food_items)-1)]
        elif category == "Transportation":
            item = transportation_items[random.randint(0,len(transportation_items)-1)]
        elif category == "Home & Utilities":
            item = home_items[random.randint(0,len(home_items)-1)]
        elif category == "Others":
            item = others_items[random.randint(0,len(others_items)-1)]
        elif category == "Entertainment":    
            item = entertainment_items[random.randint(0,len(entertainment_items)-1)]
        elif category == "Personal & Family Care":
            item = personal_items[random.randint(0,len(personal_items)-1)]
        else:
            print("error")

        transaction.append(str(item))
        transaction.append(str(random.randrange(100,10000)/100))
        transaction.append(str(category))
        transaction.append("-")
        transactions.append(','.join(transaction))
    else:
        transaction = []
        random_number_of_days = random.randrange(timeFrame)
        random_date = start_date + datetime.timedelta(days=random_number_of_days)
    
        transaction.append(str(random_date.year) + "-" + str(random_date.month).zfill(2) + "-" + str(random_date.day).zfill(2) + "T" + str(random.randint(0,23)).zfill(2) + ":" + str(random.randint(0,59)).zfill(2) + ":" + str(random.randint(0,59)).zfill(2))
        transaction.append("payment")
        transaction.append(str(random.randrange(100,100000)/100))  
        transaction.append("")
        transaction.append("+")
        transactions.append(",".join(transaction))

f = open("randomHistory.csv", 'w')
f.write('\n'.join(transactions))
