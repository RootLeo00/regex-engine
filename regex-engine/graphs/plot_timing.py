import pandas as pd
import matplotlib.pyplot as plt

# plt.style.use('style.mplstyle')

def plot_timing(file, x, y):
    
    listdf=[]
    name=[]
    fig=plt.figure(figsize=(9, 9))
    plt.subplots_adjust(left=0.25, bottom=0.15)
    print("plotting: ", file)
    i=0
    for f in file:
        #if f ends with pkl, read pickle
        if f.split(".")[1] == "pkl":
            print("reading pickle: ", f.split(".")[1])
            listdf.append(pd.read_pickle(f))
        if f.split(".")[1] == "csv":
            listdf.append(pd.read_csv(f))
        # listdf.append(pd.read_pickle(f))
        print(listdf[i])

        #retrieve engine name and pattern
        tmp=f.split("/")[-1]
        name.append(tmp.split("_")[1])
        print("name", name)

        #plot xtick every 10 elements 
        df=listdf[i]
        plt.scatter(df[x][::5], df[y][::5], label=name[i].upper(), marker='o')
        plt.plot(df[x], df[y])
        i+=1

    plt.xticks(fontsize=12)
    plt.yticks(fontsize=12)

    plt.legend()
    plt.xlabel(x, fontsize=12)
    plt.ylabel(y, fontsize=12)

    plt.savefig('./'+x+'_'+y+'.png')
    plt.show()


if __name__ == "__main__":
    # main()
    file=[ "/home/leo/github/gutenberg-app/regex-engine/output/output_automa_patternlength.csv",
          "/home/leo/github/gutenberg-app/regex-engine/output/output_kmp_patternlength.pkl",
        "/home/leo/github/gutenberg-app/regex-engine/output/output_radixtree_patternlength.pkl",
          ]
    
    plot_timing(file,  "pattern_len","time_elapsed")
    file=["/home/leo/github/gutenberg-app/regex-engine/output/output_automa_textlength.csv",
          "/home/leo/github/gutenberg-app/regex-engine/output/output_radixtree_textlength.pkl",
          "/home/leo/github/gutenberg-app/regex-engine/output/output_kmp_textlength.pkl"]
    plot_timing( file, "ncharacters", "time_elapsed")