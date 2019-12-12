# Demo script

* [Before we start](#before-we-start)
* [What are we going to show you?](#what-are-we-going-to-show-you)
* [How are we going to show it?](#how-are-we-going-to-show-it)
* [Running the demo](#running-the-demo)
  * [The network map](#the-network-map)
  * [Issuing the purchase order](#issuing-the-purchase-order)
  * [Applying for a letter of credit](#applying-for-a-letter-of-credit)
  * [Approving the letter of credit application](#approving-the-letter-of-credit-application)
  * [Shipping the goods](#shipping-the-goods)
  * [Unwinding the letter of credit](#unwinding-the-letter-of-credit)
  * [Transferring cash](#transferring-cash)
  * [Unwinding the letter of credit redux](#unwinding-the-letter-of-credit-redux)
  * [Recap](#recap)

# Before we start

Run the nodes and bring up the network map by following the instructions [in the README](https://github.com/corda/LetterOfCredit/blob/release/README.md).

# What are we going to show you?

We'll be showing you a demo that will highlight the key benefits of blockchain solutions:

- The guarantee that nodes see the exact same version of shared data
- The ability to transact without intermediaries
- The ability to exchange value

But it will also highlight the unique features of the Corda platform, features that address the challenges presented by other blockchain platforms. Corda uniquely allows:

- Privacy...
- Without foregoing a single global network...
- Of well-know counter-parties...
- With instant transaction finality...
- And the ability to model complex workflows like delivery vs. payment (DVP)

# How are we going to show it?

We will showcase the Corda platform using a real world example, a letter of credit scenario. This is one of the first use-cases going into production on the Corda platform.

What is a letter of credit? Well, suppose we have a buyer and a seller who want to enter into a trade. However, the buyer doesn't want to send payment first, and the seller doesn't want to send the goods first. They're in a stand-off.

This stand-off can be addressed using a letter of credit, which avoids the inherent trust issues in buyer/seller relationships:

- The seller issues a purchase order to the buyer
- The buyer takes this purchase order to their bank, and the bank arranges a letter of credit with the seller's bank
- The letter of credit dictates that, instead of the buyer paying the seller directly:
  - The seller's bank pays the seller
  - Then the buyer's bank pays the seller's bank
  - Then the buyer pays the buyer's bank

# Running the demo

## The network map

**Key points introduced:** Single global network, well-known counterparties via network map

**Points reinforced:**  N/A

Here is a view of the Corda network. There are four nodes that are of special interest to us:

- Lok Ma Exporters, a seller based in China
- Shenzen State Bank, the seller's bank, also based in China
- Analog Importers, a buyer based in Liverpool
- First Bank of London, the buyer's bank, based in London

All these nodes exist on the same global network. They can transact with one another directly, and with any other nodes on the global network. There are no islands, and no &quot;trapped&quot; assets.

Each node is made aware of the other nodes on the network using a signed, dynamic document called the network map. The network map links nodes to legal names and addresses.

Before joining the network, each node undergoes a KYC process before being issued a certificate. This means that:

- Nodes know exactly which legal entity they are transacting with
- Communication between nodes is encrypted using transport-layer security (TLS)

## Issuing the purchase order

**Key points introduced:** Flexible data model, need-to-know data distribution

**Points reinforced:** Network map

We can see on the left the series of steps in this letter of credit scenario. We'll go through them one-by-one. We start by issuing an purchase order from the seller to the buyer.

**<Click the first step>**

Here's the seller's view of the ledger. There's nothing here yet. Let's start the process by creating an purchase order.

**<Click 'Create Purchase Order'>**

These are all the fields that are included in this application's representation of an purchase order. The Corda data model is completely flexible. We can have any fields we like, of any type.

For the purposes of the demo, we'll auto-complete most fields.

**<Click 'Autocomplete fields'>**

However, we still need to pick the buyer. Using the network map, it's very easy to look up the well-known counterparty we want to transact with by name.

Let's commit this purchase order to the ledger.

**<Click 'Create Purchase Order'>**

If we look at the bottom of the seller's view, we can see the transaction has already been committed to the ledger.

**<Close the seller's modal and open the buyer's modal>**

If we open the buyer's view, we can see that they see the exact same transaction and purchase order as the seller, since they were involved in the transaction.

**<Close the buyer's modal and open the seller's bank's modal>**

However, if we open the view of the other nodes - the buyer's bank or the seller's bank, for example - we can see that they aren't even aware of this transaction, even though they're on the same network. In Corda, ledger updates are distributed to nodes on a need-to-know basis. Because the purchase order only involves the buyer and the seller, only they have seen the transaction.

## Applying for a letter of credit

**Key points introduced:** Immutability, double-spend prevention

**Points reinforced:** Flexible data model, need-to-know data distribution

**<Close the seller's bank's modal and click on the second step>**

Going back to the buyer's view, we can apply for a letter of credit. Remember, this is an agreement between the buyer and seller and their respective banks that payment will be made via the banks for trust purposes.

**<Click 'Apply for LOC'>**

Again, note that the data model is completely flexible. This is the set of fields this application uses for letter of credit applications. They're different from the purchase order fields.

We're going to autocomplete again...

**<Click 'Autocomplete fields'>**

But note again how we can quickly pick the banks involved using their legal names from the network map.

We finalise the application.

**<Click 'Apply'>**

The transaction is now committed to the ledger. Looking at the transaction, we can see that:

- The transaction has been signed by the buyer, meaning that:
  - It cannot be modified. Any attempt to modify it now would invalidate the buyer's signature
  - It cannot be repudiated. The buyer's bank can prove that the buyer applied for a letter of credit
- The transaction has also been signed by the notary pool. The signature from the notary pool means that the transaction does not represent a double-spend attempt. Notary pools are services on the network that can use any algorithm to prevent double-spends

Let's try and apply for the same purchase order twice.

**<Click 'Apply for LOC', then 'Autocomplete fields', then 'Apply'>**

The application will fail because we have already used this purchase order to apply for a letter of credit.

**<Close the buyer's modal and open the modal of the seller>**

And again, we can see that this ledger update has only been propagated to the parties involved - the buyer and the buyer's bank.

## Approving the letter of credit application

**Key points introduced:** UTXO model

**Points reinforced:** Immutability and double-spend prevention

**<Close the seller's modal and click on the third step>**

Now going to the buyer's bank's view, we can approve the letter of credit.

**<Click 'Approve'>**

Let's take a look at this transaction:

- We have &quot;consumed&quot; the existing letter of credit application and purchase order
- We have created a letter of credit

This is how the Corda ledger is updated. We consume some existing set of states to create a new set of states.

Again, the signatures by the buye's bank and the notary pool make the transaction immutable, irrepudiable, and immune to double-spend attempts.

## Shipping the goods

**Key points introduced:** Shared view of the data, smart contract logic

**Points reinforced:** UTXO model

**<Close the buyer's bank's modal and click on the fourth step>**

With the letter of credit in place, the seller can add the bill of lading and ship the goods. The bill of lading is a legal document indicating who has title to the goods shipped by the seller.

**<Click 'Create Bill of Lading', then 'Autocomplete fields', then 'Submit'>**

The status of the letter of credit has been updated to &quot;LADED&quot;. Looking at the transactions view, we can see that the letter of credit with a status of &quot;ISSUED&quot; has been consumed to create the letter of credit with a status of &quot;LADED&quot;.

What happens if we try and add the bill of lading twice?

**<Click 'Create Bill of Lading', then 'Autocomplete fields', then 'Submit'>**

The smart contract logic underlying the letter of credit rejects this attempt to add a bill of lading twice. The requirement for the input letter of credit to have a status of &quot;ISSUED&quot; is not satisfied, since the order is already laded.

Now let's ship the goods.

**<Click 'Ship'>**

Instantly, the status of the letter of credit is updated to shipped. Every other node who is tracking the letter of credit automatically sees their ledger updated to show the new status. For example, if we open the buyer's modal...

**<Close the seller's modal and open the buyer's modal>**

We see that the status of the letter of credit has been updated.

## Unwinding the letter of credit

**Key points introduced:** Atomicity, instant finality

**Points reinforced:** Smart contract logic

**<Close the buyer's modal>**

It's now time to unwind the letter of credit:

- The seller's bank needs to pay the seller...
- So that the buyer's bank can pay the seller's bank...
- So that the buyer's can pay its bank

**<Click on the fifth step>**

Currently, the bill of lading is held by the seller.

**<Click 'View' on the bill of lading, then click the sliders in the top-right>**

Let's pay the seller in exchange for the bill of lading.

**<Click 'Pay Seller'>**

The status of the bill of lading is updated to say &quot;BENEFICIARY\_PAID&quot;.

We can now see that the bill of lading is held by the seller's bank.

**<Click 'View' on the bill of lading, then click the sliders in the top-right>**

If we look at the transaction, we can see that it is an atomic delivery-versus-payment transaction. The money moved from the seller's bank to the seller at exactly the same time as the bill of lading moved from the seller to the seller's bank. It is impossible for only part of the transaction to go through.

It's also important to note that transaction finality is instant. As soon as the required signatures - including the notary pool's signature - have been applied, the transaction is final. There is no possibility of the transaction being reversed, or a need to wait for a number of &quot;confirmations&quot;.

If we try and pay again...

**<Click 'Pay Seller'>**

Again, the smart contract logic underlying the letter of credit rejects this attempt to pay the seller twice. The requirement for the seller not to have already paid is not satisfied, since the seller has already been paid.

**<Close the seller's bank's modal and click on the sixth step>**

Now the buyer's bank pays the seller's bank in exchange for the bill of lading.

**<Click 'Pay Advising Bank'>**

Again, the letter of credit's status is updated.

We can now see that the bill of lading is held by the buyer's bank.

**<Click 'View' on the bill of lading, then click the sliders in the top-right, then close the buyer's bank's modal and click on the seventh step>**

Finally, the buyer exchanges its cash for the bill of lading, completing the full cycle.

**<Click 'Settle'>**

Oh...

So, uh... So the buyer doesn't actually have any money. We forgot to give him any cash...

But it might actually be ok.

## Transferring cash

**Key points introduced:** Interoperability between applications

**Points reinforced:** N/A

In Corda, all applications run side-by-side on a single network. In our case, as well as the letter of credit CorDapp, the bank nodes have a cash CorDapp installed.

These two CorDapps were developed completely in isolation:

- The cash CorDapp was built by a software consultancy called Giant Machines. It is inspired by Project Jasper, a Central Bank of Corda project on Corda
- The letter of credit CorDapp was built by members of the R3 consortium

And yet the cash generated by the cash CorDapp is completely usable as an input into the letter of credit CorDapp. The Corda network is completely free of silos that restrict digital assets to certain business networks or sets of nodes.

Let's issue that cash now.

**<Close the buyer's modal, then click 'Powered by Corda', then tell everyone to watch the series of steps on the left-hand side. An extra step will appear>**

We'll transfer some cash from the buyer's bank to the buyer so that he can pay off the letter of credit.

**<Click the seventh step, then click 'Transfer', then transfer USD2mn to Analog Imports>**

## Unwinding the letter of credit redux

**Key points introduced:** N/A

**Points reinforced:** N/A

Finally, we can make the final payment and completely unwind the letter of credit.

**<Close the cash issuance modal, then click the eight step, then click 'Settle'>**

Finally, the buyer has the bill of lading and owns the goods.

**<Click 'View' on the bill of lading, then click the sliders in the top-right>**

## Recap

This demo has highlighted some of the key benefits of blockchain solutions generally:

- The guarantee that when two parties see the same fact, they see the exact same version of that fact
- The ability for parties to transact without intermediaries
- The ability for parties to exchange value

But it has also highlighted some unique features of the Corda platform, features that address the challenges presented by other blockchain platforms:

- Corda uniquely allows privacy, with data only distributed on a need-to-know basis. For example, when the seller and the buyer originally agreed the purchase order, their banks were not aware of this fact
- And importantly, this privacy was achieved in a single global network. Any node can transact directly with any other node, and assets can move across use-cases
- The nodes on the network all have well-known identities, meaning that you know exactly who you're transacting with
- Transaction finality is instant. There's no need to wait for a number of confirmations
- Corda allows you to model complex workflows easily, like the letter of credit workflow we saw today
